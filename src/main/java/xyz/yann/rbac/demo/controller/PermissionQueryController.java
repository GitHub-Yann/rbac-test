package xyz.yann.rbac.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.yann.rbac.demo.controller.mapper.DtoMapper;
import xyz.yann.rbac.demo.domain.MemberRoleEntity;
import xyz.yann.rbac.demo.dto.EffectivePermissionResponse;
import xyz.yann.rbac.demo.dto.GrantPayloadItem;
import xyz.yann.rbac.demo.service.MemberRoleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class PermissionQueryController {

    private final MemberRoleService memberRoleService;

    public PermissionQueryController(MemberRoleService memberRoleService) {
        this.memberRoleService = memberRoleService;
    }

    @GetMapping("/effective-permissions")
    public EffectivePermissionResponse queryEffectivePermissions(@RequestParam("principal") Long principalId) {
        List<MemberRoleEntity> assignments = memberRoleService.findByMemberId(principalId);
        EffectivePermissionResponse response = new EffectivePermissionResponse();
        // response.setPrincipal(principalId);
        // response.setRoles(assignments.stream().map(memberRole -> memberRole.getRole().getRoleCode()).toList());
        // List<GrantPayloadItem> permissions = assignments.stream()
        //         .map(MemberRoleEntity::getRole)
        //         .flatMap(role -> DtoMapper.toGrantPayloadItems(role.getGrantPayload()).stream())
        //         .toList();
        // response.setPermissions(permissions);
        return response;
    }
}
