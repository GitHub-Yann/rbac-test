package xyz.yann.rbac.demo.security.cache;

import java.util.ArrayList;
import java.util.List;

public class PrincipalPermissionView {

    private Long principalId;
    private List<RolePermissionView> roles = new ArrayList<>();

    public PrincipalPermissionView() {
    }

    public PrincipalPermissionView(Long principalId, List<RolePermissionView> roles) {
        this.principalId = principalId;
        this.roles = roles;
    }

    public Long getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    public List<RolePermissionView> getRoles() {
        return roles;
    }

    public void setRoles(List<RolePermissionView> roles) {
        this.roles = roles;
    }
}
