package xyz.yann.rbac.demo.dto;

import java.util.List;

public class DecisionResponse {

    private boolean allowed;
    private List<String> matchedRoles;
    private String message;

    public DecisionResponse() {
    }

    public DecisionResponse(boolean allowed, List<String> matchedRoles, String message) {
        this.allowed = allowed;
        this.matchedRoles = matchedRoles;
        this.message = message;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public List<String> getMatchedRoles() {
        return matchedRoles;
    }

    public void setMatchedRoles(List<String> matchedRoles) {
        this.matchedRoles = matchedRoles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
