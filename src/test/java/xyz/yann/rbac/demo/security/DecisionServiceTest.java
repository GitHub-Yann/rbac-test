package xyz.yann.rbac.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.yann.rbac.demo.dto.CreateResourceRequest;
import xyz.yann.rbac.demo.dto.CreateRoleRequest;
import xyz.yann.rbac.demo.dto.GrantPayloadItem;
import xyz.yann.rbac.demo.dto.MemberRoleAssignmentRequest;
import xyz.yann.rbac.demo.service.MemberRoleService;
import xyz.yann.rbac.demo.service.ResourceService;
import xyz.yann.rbac.demo.service.RoleService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DecisionServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MemberRoleService memberRoleService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DecisionService decisionService;

    @Test
    void shouldAllowWhenUserHasGrant() {
        CreateResourceRequest resourceRequest = new CreateResourceRequest();
        resourceRequest.setDomain("service");
        resourceRequest.setType("spring_service");
        resourceRequest.setResourceKey("service:spring_service:deploy-service");
        resourceRequest.setResourceName("Deploy Service");
        resourceService.createResource(resourceRequest);

        CreateRoleRequest createRoleRequest = new CreateRoleRequest();
        createRoleRequest.setRoleCode("deploy_op");
        createRoleRequest.setRoleName("Deploy Operator");
        GrantPayloadItem payloadItem = new GrantPayloadItem();
        payloadItem.setResourceKey("service:spring_service:deploy-service");
        payloadItem.setActions(List.of("deploy", "rollback"));
        createRoleRequest.setGrants(List.of(payloadItem));
        var role = roleService.createRole(createRoleRequest);

        MemberRoleAssignmentRequest assignmentRequest = new MemberRoleAssignmentRequest();
        memberRoleService.assignRole(100L, role.getId(), assignmentRequest);

        var decision = decisionService.decide(100L, "service:spring_service:deploy-service", "deploy");
        assertThat(decision.isAllowed()).isTrue();
        assertThat(decision.getMatchedRoles()).containsExactly("deploy_op");
    }
}
