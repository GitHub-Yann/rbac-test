package xyz.yann.rbac.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.yann.rbac.demo.controller.mapper.DtoMapper;
import xyz.yann.rbac.demo.dto.CreateRoleRequest;
import xyz.yann.rbac.demo.dto.RoleResponse;
import xyz.yann.rbac.demo.dto.UpdateRoleRequest;
import xyz.yann.rbac.demo.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse createRole(@RequestBody @Valid CreateRoleRequest request) {
        return DtoMapper.toRoleResponse(roleService.createRole(request));
    }

    @PutMapping("/{roleId}")
    public RoleResponse updateRole(@PathVariable Long roleId, @RequestBody @Valid UpdateRoleRequest request) {
        return DtoMapper.toRoleResponse(roleService.updateRole(roleId, request));
    }

    @GetMapping
    public List<RoleResponse> listRoles() {
        return roleService.listRoles().stream()
                .map(DtoMapper::toRoleResponse)
                .toList();
    }
}
