package xyz.yann.rbac.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import xyz.yann.rbac.demo.domain.PermissionEntity;
import xyz.yann.rbac.demo.domain.ResourceEntity;
import xyz.yann.rbac.demo.dto.CreatePermissionRequest;
import xyz.yann.rbac.demo.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ResourceService resourceService;

    public PermissionService(PermissionRepository permissionRepository, ResourceService resourceService) {
        this.permissionRepository = permissionRepository;
        this.resourceService = resourceService;
    }

    public List<PermissionEntity> addPermissions(Long resourceId, List<CreatePermissionRequest> requests) {
        ResourceEntity resource = resourceService.getById(resourceId);
        return requests.stream()
                .map(request -> addPermission(resource, request))
                .toList();
    }

    private PermissionEntity addPermission(ResourceEntity resource, CreatePermissionRequest request) {
        permissionRepository.findByResourceIdAndActionCode(resource.getId(), request.getActionCode())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "permission already exists");
                });
        PermissionEntity entity = new PermissionEntity();
        entity.setResource(resource);
        entity.setActionCode(request.getActionCode());
        entity.setActionName(request.getActionName());
        entity.setDescription(request.getDescription());
        return permissionRepository.save(entity);
    }

    public List<PermissionEntity> listByResource(Long resourceId) {
        resourceService.getById(resourceId);
        return permissionRepository.findByResourceId(resourceId);
    }
}
