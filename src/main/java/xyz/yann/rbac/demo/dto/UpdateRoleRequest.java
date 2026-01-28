package xyz.yann.rbac.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateRoleRequest {

    @NotBlank
    private String roleName;
    private String description;
    private boolean enabled = true;
    @NotEmpty
    private List<GrantPayloadItem> grants;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<GrantPayloadItem> getGrants() {
        return grants;
    }

    public void setGrants(List<GrantPayloadItem> grants) {
        this.grants = grants;
    }
}
