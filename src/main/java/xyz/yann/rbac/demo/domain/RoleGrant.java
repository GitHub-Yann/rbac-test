package xyz.yann.rbac.demo.domain;

import java.util.List;

public class RoleGrant {

    private String resourceKey;
    private List<String> actions;

    public RoleGrant() {
    }

    public RoleGrant(String resourceKey, List<String> actions) {
        this.resourceKey = resourceKey;
        this.actions = actions;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}
