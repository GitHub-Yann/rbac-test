package xyz.yann.rbac.demo.controller.mapper;

import xyz.yann.rbac.demo.domain.MemberRoleEntity;
import xyz.yann.rbac.demo.domain.ResourceEntity;
import xyz.yann.rbac.demo.domain.RoleEntity;
import xyz.yann.rbac.demo.domain.RoleGrant;
import xyz.yann.rbac.demo.dto.GrantPayloadItem;
import xyz.yann.rbac.demo.dto.MemberRoleResponse;
import xyz.yann.rbac.demo.dto.ResourceResponse;
import xyz.yann.rbac.demo.dto.RoleResponse;
import xyz.yann.rbac.demo.util.RoleGrantUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static ResourceResponse toResourceResponse(ResourceEntity entity) {
        ResourceResponse response = new ResourceResponse();
        response.setId(entity.getId());
        response.setDomain(entity.getDomain());
        response.setType(entity.getType());
        response.setResourceKey(entity.getResourceKey());
        response.setResourceName(entity.getResourceName());
        response.setMetadata(entity.getMetadata());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public static RoleResponse toRoleResponse(RoleEntity role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRoleCode(role.getRoleCode());
        response.setRoleName(role.getRoleName());
        response.setDescription(role.getDescription());
        response.setEnabled(role.isEnabled());
        response.setGrants(toGrantPayloadItems(RoleGrantUtils.fromPermissions(role.getPermissions())));
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());
        return response;
    }

    public static List<GrantPayloadItem> toGrantPayloadItems(List<RoleGrant> grants) {
        return grants.stream()
                .map(grant -> {
                    GrantPayloadItem item = new GrantPayloadItem();
                    item.setResourceKey(grant.getResourceKey());
                    item.setActions(grant.getActions());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public static MemberRoleResponse toMemberRoleResponse(MemberRoleEntity entity) {
        MemberRoleResponse response = new MemberRoleResponse();
        response.setId(entity.getId());
        response.setMemberId(entity.getMemberId());
        response.setMemberName(String.valueOf(entity.getMemberId()));
        response.setRoleId(entity.getRole().getId());
        response.setRoleName(entity.getRole().getRoleName());
        response.setRoleCode(entity.getRole().getRoleCode());
        return response;
    }
}
