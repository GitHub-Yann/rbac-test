package xyz.yann.rbac.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import xyz.yann.rbac.demo.domain.MemberRoleEntity;
import xyz.yann.rbac.demo.domain.RoleEntity;
import xyz.yann.rbac.demo.dto.MemberRoleAssignmentRequest;
import xyz.yann.rbac.demo.repository.MemberRoleRepository;
import xyz.yann.rbac.demo.security.cache.PermissionCacheService;

@Service
public class MemberRoleService {

    private final RoleService roleService;
    private final PermissionCacheService permissionCacheService;
    private final MemberRoleRepository repository;

    public MemberRoleService(MemberRoleRepository repository,
                             RoleService roleService,
                             PermissionCacheService permissionCacheService) {
        this.repository = repository;
        this.roleService = roleService;
        this.permissionCacheService = permissionCacheService;
    }

    public MemberRoleEntity assignRole(Long memberId, Long roleId, MemberRoleAssignmentRequest request) {
        RoleEntity role = roleService.getById(roleId);
        repository.findByMemberIdAndRole_Id(memberId, roleId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "重复绑定角色");
                });
        MemberRoleEntity entity = new MemberRoleEntity();
        entity.setMemberId(memberId);
        entity.setRole(role);
        MemberRoleEntity saved = repository.save(entity);
        permissionCacheService.evict(memberId);
        return saved;
    }

    public void revokeRole(Long memberId, Long roleId) {
        MemberRoleEntity entity = repository.findByMemberIdAndRole_Id(memberId, roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户未拥有该角色"));
        repository.delete(entity);
        permissionCacheService.evict(memberId);
    }

    public List<MemberRoleEntity> findByMemberId(Long memberId) {
        return repository.findByMemberId(memberId);
    }

    public List<MemberRoleEntity> findAllAssignments() {
        return repository.findAll();
    }
}
