package xyz.yann.rbac.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xyz.yann.rbac.demo.domain.PermissionEntity;
import xyz.yann.rbac.demo.dto.CreatePermissionRequest;
import xyz.yann.rbac.demo.service.PermissionService;

@RestController
@RequestMapping("/api/v1/resources/{resourceId}/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<PermissionEntity> createPermissions(@PathVariable Long resourceId,
                                                    @RequestBody @Valid List<CreatePermissionRequest> requests) {
        return permissionService.addPermissions(resourceId, requests);
    }

    @GetMapping
    public List<PermissionEntity> listPermissions(@PathVariable Long resourceId) {
        return permissionService.listByResource(resourceId);
    }
}
