package xyz.yann.rbac.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateRoleRequest {

    @NotBlank
    private String roleCode;
    @NotBlank
    private String roleName;
    private String description;
    @NotEmpty
    private List<GrantPayloadItem> grants;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GrantPayloadItem> getGrants() {
        return grants;
    }

    public void setGrants(List<GrantPayloadItem> grants) {
        this.grants = grants;
    }
}
