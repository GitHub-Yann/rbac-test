package xyz.yann.rbac.demo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import xyz.yann.rbac.demo.domain.MemberRoleEntity;
import xyz.yann.rbac.demo.dto.DecisionResponse;
import xyz.yann.rbac.demo.security.cache.PermissionCacheService;
import xyz.yann.rbac.demo.security.cache.PrincipalPermissionView;
import xyz.yann.rbac.demo.security.cache.RolePermissionView;
import xyz.yann.rbac.demo.service.MemberRoleService;
import xyz.yann.rbac.demo.util.RoleGrantUtils;

@Service
public class DecisionService {

    private final MemberRoleService memberRoleService;
    private final PermissionCacheService permissionCacheService;

    public DecisionService(MemberRoleService memberRoleService,
                           PermissionCacheService permissionCacheService) {
        this.memberRoleService = memberRoleService;
        this.permissionCacheService = permissionCacheService;
    }

    /**
     * 基于主体权限视图判断请求资源与动作是否被授权。
     * 仅考虑启用角色，并收集所有命中授权的角色编码返回。
     */
    public DecisionResponse decide(Long principalId, String resourceKey, String actionCode) {
        PrincipalPermissionView view = fetchPrincipalView(principalId);
        List<RolePermissionView> eligibleRoles = view.getRoles().stream()
                .filter(RolePermissionView::isEnabled)
                .collect(Collectors.toList());

        List<String> matchedRoles = new ArrayList<>();
        for (RolePermissionView role : eligibleRoles) {
            boolean match = role.getGrants()
                    .stream()
                    .filter(grant -> Objects.equals(grant.getResourceKey(), resourceKey))
                    .anyMatch(grant -> grant.getActions().contains(actionCode));
            if (match) {
                matchedRoles.add(role.getRoleCode());
            }
        }
        boolean allowed = !matchedRoles.isEmpty();
        String message = allowed ? "access granted" : "no matching permission";
        return new DecisionResponse(allowed, matchedRoles, message);
    }

    /**
     * 先查询本地/远程缓存，未命中时回源构建权限视图并写入缓存。
     */
    private PrincipalPermissionView fetchPrincipalView(Long principalId) {
        return permissionCacheService.getFromLocal(principalId)
                .or(() -> permissionCacheService.getFromRemote(principalId)
                        .map(view -> {
                            permissionCacheService.cache(principalId, view);
                            return view;
                        }))
                .orElseGet(() -> {
                    PrincipalPermissionView view = buildPrincipalView(principalId);
                    permissionCacheService.cache(principalId, view);
                    return view;
                });
    }

    /**
     * 依据成员与角色关系拼装权限视图，供缓存和决策逻辑复用。
     */
    private PrincipalPermissionView buildPrincipalView(Long principalId) {
        List<MemberRoleEntity> assignments = memberRoleService.findByMemberId(principalId);
        List<RolePermissionView> roles = assignments.stream()
                .map(MemberRoleEntity::getRole)
                .map(role -> new RolePermissionView(role.getRoleCode(), role.isEnabled(),
                        RoleGrantUtils.fromPermissions(role.getPermissions())))
                .collect(Collectors.toList());
        return new PrincipalPermissionView(principalId, roles);
    }
}
