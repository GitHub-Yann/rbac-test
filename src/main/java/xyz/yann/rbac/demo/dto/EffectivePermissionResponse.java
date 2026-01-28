package xyz.yann.rbac.demo.dto;

import java.util.List;

public class EffectivePermissionResponse {

    private Long principal;
    private List<String> roles;
    private List<GrantPayloadItem> permissions;

    public Long getPrincipal() {
        return principal;
    }

    public void setPrincipal(Long principal) {
        this.principal = principal;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<GrantPayloadItem> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<GrantPayloadItem> permissions) {
        this.permissions = permissions;
    }
}
