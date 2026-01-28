package xyz.yann.rbac.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.yann.rbac.demo.domain.PermissionEntity;
import xyz.yann.rbac.demo.domain.ResourceEntity;
import xyz.yann.rbac.demo.domain.RoleEntity;
import xyz.yann.rbac.demo.dto.CreateRoleRequest;
import xyz.yann.rbac.demo.dto.GrantPayloadItem;
import xyz.yann.rbac.demo.dto.UpdateRoleRequest;
import xyz.yann.rbac.demo.repository.PermissionRepository;
import xyz.yann.rbac.demo.repository.ResourceRepository;
import xyz.yann.rbac.demo.repository.RoleRepository;
import xyz.yann.rbac.demo.security.cache.PermissionCacheService;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionCacheService permissionCacheService;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
                       PermissionCacheService permissionCacheService,
                       ResourceRepository resourceRepository,
                       PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionCacheService = permissionCacheService;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
    }

    public RoleEntity createRole(CreateRoleRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "role code already exists");
        }
        RoleEntity entity = new RoleEntity();
        entity.setRoleCode(request.getRoleCode());
        entity.setRoleName(request.getRoleName());
        entity.setDescription(request.getDescription());
        entity.setPermissions(resolvePermissions(request.getGrants()));
        return roleRepository.save(entity);
    }

    public RoleEntity updateRole(Long roleId, UpdateRoleRequest request) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setEnabled(request.isEnabled());
        role.setPermissions(resolvePermissions(request.getGrants()));
        RoleEntity saved = roleRepository.save(role);
        permissionCacheService.evictAll();
        return saved;
    }

    public List<RoleEntity> listRoles() {
        return roleRepository.findAll();
    }

    public RoleEntity getById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));
    }

    private Set<PermissionEntity> resolvePermissions(List<GrantPayloadItem> grants) {
        Map<String, ResourceEntity> resourceCache = new HashMap<>();
        Set<PermissionEntity> permissions = new HashSet<>();
        for (GrantPayloadItem grant : grants) {
            ResourceEntity resource = resourceCache.computeIfAbsent(grant.getResourceKey(), key ->
                    resourceRepository.findByResourceKey(key)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "resource not found: " + key)));
            for (String action : grant.getActions()) {
                String actionCode = action == null ? null : action.trim();
                if (actionCode == null || actionCode.isEmpty()) {
                    continue;
                }
                PermissionEntity permission = permissionRepository
                        .findByResourceIdAndActionCode(resource.getId(), actionCode)
                        .orElseGet(() -> {
                            PermissionEntity entity = new PermissionEntity();
                            entity.setResource(resource);
                            entity.setActionCode(actionCode);
                            entity.setActionName(actionCode);
                            entity.setDescription(null);
                            return permissionRepository.save(entity);
                        });
                permissions.add(permission);
            }
        }
        return permissions;
    }
}
