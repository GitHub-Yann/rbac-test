# RBAC 权限管理设计文档

## 1. 设计目标与范围
- 在不破坏现有 `memberinfo` 用户表的前提下，构建一个覆盖 API、内部服务、Kubernetes 资源的最小可用 RBAC 能力。
- 以“资源 + 权限”模型统一登记受控对象，不再依赖历史 UI 表或硬编码权限常量。
- 采用 Spring Boot + Spring Security，业务服务通过 PEP Starter 接入，当前工程承担 PDP/PAP 职责，负责鉴权、缓存与审计。
## 2. 资源与权限抽象
### 2.1 资源模型
| 域 (`domain`) | 类型 (`type`) 示例 | 唯一标识示例 | 元数据示例 |
| --- | --- | --- | --- |
| `api` | `rest`, `rpc`, `internal` | `api:rest:svc-user-center:GET:/users` | HTTP Method、Path、所属服务 |
| `service` | `spring_service`, `pipeline`, `job` | `service:spring_service:deploy-service` | 环境、Git 仓库、负责人 |
| `k8s` | `namespace`, `deployment`, `pod`, `configmap` | `k8s:namespace:gateway-s` | 集群、命名空间、标签 |
| `custom` | 业务自定义类型 | `custom:dataset:dw_user` | 数据域、敏感级别 |

- `domain`：资源所属的大类，用于做租户或产品划分。
- `type`：域内更细粒度的资源分类，必要时可扩展；若无区分可使用 `default`。
- `resource_key`：同域同类型下的唯一标识，例如 `svc-user-center:GET:/users`、`gateway-s`。

### 2.2 权限集合
- API：初期只区分“是否可调用”，统一使用 `invoke`，后续可扩展 `publish`、`deprecate` 等。
- 服务：`create`、`build`、`deploy`、`rollback`、`start`、`stop` 等生命周期动作。
- Kubernetes：`create_namespace`、`delete_namespace`、`restart_pod`、`scale_deployment`、`edit_configmap`。
- 自定义资源：各业务按需定义，建议保持蛇形命名，如 `grant_access`、`approve_task`。

## 3. 数据模型
### 3.1 核心概念
- **User/ServiceAccount**：沿用 `memberinfo`，仅保留基础身份字段，角色关系迁移到独立表。
- **Resource**：`iam_resource` 中记录受控对象及元数据，支持 JSON 扩展。
- **Permission**：`iam_permission` 以“资源 + 动作”粒度存储可授权项，包含展示名称与描述。
- **Role**：`iam_role` 表示职责集合，通过 `iam_role_permission` 关联多个 Permission。
- **MemberRole**：`iam_member_role` 维护成员与角色的绑定，可扩展生效时间、审批信息。

### 3.2 表结构（与实体保持一致）
```sql
CREATE TABLE `iam_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `domain` varchar(64) NOT NULL,
  `type` varchar(64) NOT NULL,
  `resource_key` varchar(256) NOT NULL,
  `resource_name` varchar(128) NOT NULL,
  `metadata` longtext,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_key` (`resource_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `iam_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource_id` bigint NOT NULL,
  `action_code` varchar(64) NOT NULL,
  `action_name` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_resource_action` (`resource_id`,`action_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `iam_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL,
  `role_name` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `iam_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `iam_member_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL COMMENT '引用 memberinfo.id',
  `role_id` bigint NOT NULL COMMENT '引用 iam_role.id',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_role` (`member_id`,`role_id`),
  KEY `idx_member_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
> `metadata` 通过 `MapJsonConverter` 与 `Map<String,Object>` 互转；`iam_permission` 记录动作编码、名称、描述，`iam_role_permission` 负责角色与权限绑定，便于 SQL 查询及审计。外键约束根据部署需要可选择在 DB 层开启或由应用保证。

#### 测试数据示例
```sql
INSERT INTO `iam_resource` (`id`,`domain`,`type`,`resource_key`,`resource_name`,`metadata`,`created_at`,`updated_at`,`version`) VALUES
(1,'api','rest','api:rest:iam-demo:GET:/api/v1/roles','角色查询接口','{"method":"GET","path":"/api/v1/roles"}',NOW(),NOW(),0),
(2,'service','spring_service','service:spring_service:iam-auth-service','IAM 鉴权服务','{"env":"prod","owner":"iam-team"}',NOW(),NOW(),0);

INSERT INTO `iam_permission` (`id`,`resource_id`,`action_code`,`action_name`,`description`,`created_at`) VALUES
(1,1,'invoke','调用 API','允许调用 RBAC REST 接口',NOW()),
(2,2,'deploy','服务部署','允许部署 IAM 鉴权服务',NOW());

INSERT INTO `iam_role` (`id`,`role_code`,`role_name`,`description`,`enabled`,`created_at`,`updated_at`,`version`) VALUES
(1,'iam_admin','IAM 平台管理员','管理 IAM 资源与角色',b'1',NOW(),NOW(),0),
(2,'iam_viewer','IAM 观察员','只读访问接口',b'1',NOW(),NOW(),0);

INSERT INTO `iam_role_permission` (`role_id`,`permission_id`) VALUES
(1,1),(1,2),(2,1);

INSERT INTO `iam_member_role` (`id`,`member_id`,`role_id`,`created_at`,`updated_at`) VALUES
(1,10001,1,NOW(),NOW()),
(2,10002,2,NOW(),NOW());
```

### 3.3 授权扩展路线
1. **当前实现**：判定逻辑 = `iam_member_role`（找出成员角色） + `iam_role_permission` + `iam_permission`（展开资源与动作列表）。
2. **下一阶段**：在 `iam_permission` 中补充 effect、条件标签，`iam_role_permission` / `iam_member_role` 添加审批人、有效期等字段，满足审计与回滚需求。

## 4. 服务模块划分
1. **iam-auth-service（PDP/PAP/PIP）**：提供 `DecisionService#decide` 和 `/decision` 接口，承担鉴权与缓存。
2. **iam-resource-service**：资源 CRUD，可接入 OpenAPI、Kubernetes、CI/CD 导入任务。
3. **iam-role-service**：角色 CRUD，维护 `iam_role_permission` 绑定与历史版本。
4. **iam-member-service**：封装成员-角色读写，提供单人编辑、批量导入接口，并与组织架构同步。
5. **iam-starter（PEP）**：供业务应用嵌入，通过拦截器/AOP 调用 PDP，并预留 HTTP/RPC 集成能力。

## 5. 管理接口与流程
### 5.1 管理 API
- `POST /api/v1/resources`：注册或批量导入资源。
- `POST /api/v1/resources/{id}/permissions`：为资源登记可授权的动作。
- `POST/PUT/GET /api/v1/roles`：角色 CRUD，接口仍接收 `resourceKey + actions`，服务层负责维护 `iam_permission`、`iam_role_permission`。
- `POST/DELETE /api/v1/members/{memberId}/roles/{roleId}`：新增或删除成员-角色绑定，可扩展生效时间。
- `GET /api/v1/effective-permissions?principal={id}`：根据 `iam_member_role` + `iam_role_permission` 生成扁平权限视图。

### 5.2 鉴权流程
1. PEP Starter 在请求进入 Controller 前解析主体（JWT/Session）。
2. PEP 根据路由、HTTP 方法、业务标签推断 `resourceKey + actionCode`，调用 PDP `/decision`。
3. PDP 先查本地缓存→Redis→若未命中则联表查询 `iam_member_role`、`iam_role_permission`、`iam_permission` 判定是否允许。
4. PDP 返回 Allow/Deny 及命中角色信息，PEP 放行或抛出 403，并记录关键审计字段。
5. 高危操作沿用相同链路，确保所有敏感操作可追溯。

### 5.3 资源同步
- **API 资产**：发布流程解析 OpenAPI，调用资源服务写入 Endpoint。
- **服务资产**：CI/CD 创建服务环境时同步资源。
- **Kubernetes 资产**：Operator/定时任务发现 Namespace、Deployment、Pod，自动生成资源与权限。
- **自定义域**：业务系统通过资源 API 注册自定义对象，如数据集、消息队列等。

## 6. 审计与合规
- 记录角色、资源、成员绑定的变更，可写独立表或发送至 ELK/SIEM。
- 运行态敏感操作记录请求、决策结果与 `request_id`，便于与业务日志关联。
- 若需审批流程，可新增 `iam_audit_log` 并与 `iam_member_role`、`iam_role_permission` 变更联动。

## 7. 性能与缓存策略
- PDP 本地缓存主体的扁平权限列表，`iam_role_permission`、`iam_member_role` 变更后通过事件或手动 Evict 失效缓存。
- 使用布隆过滤器或位图快速判断“成员在某域是否有权限”，减少无谓查询。
- 大规模授权通过异步作业执行，并提供进度与结果查询。

## 8. 开发与测试建议
- **单元测试**：覆盖资源/权限/角色服务，重点校验 `iam_permission`、`iam_role_permission` 绑定与缓存生成。
- **集成测试**：模拟 `PEP -> PDP` 链路，结合 Testcontainers MySQL 验证缓存命中与回源。
- **契约测试**：针对 Kubernetes 操作封装 Mock API Server，确保权限校验在真实调用前完成。

## 9. 推进计划
1. 落地上述五张基础表并生成 JPA 实体，完成资源/权限/角色/成员 API。
2. 将存量代码迁移到新的 `iam_member_role` + `iam_permission` 模型，闭环“资源注册→角色授权→成员绑定→PDP 判定”。
3. 接入网关、CI/CD、Kubernetes 等渠道，自动同步资源和权限，减少人工维护。
4. 随业务增长扩展 `iam_permission`、`iam_role_permission` 的 effect、审批、有效期信息，并补充 `iam_audit_log` 等能力，持续演进为完整 RBAC 平台。

