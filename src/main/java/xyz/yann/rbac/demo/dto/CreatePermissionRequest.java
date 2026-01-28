package xyz.yann.rbac.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePermissionRequest {

    @NotBlank
    private String actionCode;
    @NotBlank
    private String actionName;
    private String description;

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
