package xyz.yann.rbac.demo.security.cache;

import xyz.yann.rbac.demo.domain.RoleGrant;

import java.util.List;

public class RolePermissionView {

    private String roleCode;
    private boolean enabled;
    private List<RoleGrant> grants;

    public RolePermissionView() {
    }

    public RolePermissionView(String roleCode, boolean enabled, List<RoleGrant> grants) {
        this.roleCode = roleCode;
        this.enabled = enabled;
        this.grants = grants;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<RoleGrant> getGrants() {
        return grants;
    }

    public void setGrants(List<RoleGrant> grants) {
        this.grants = grants;
    }
}
