package xyz.yann.rbac.demo.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class CreateResourceRequest {

    @NotBlank
    private String domain;
    @NotBlank
    private String type;
    @NotBlank
    private String resourceKey;
    @NotBlank
    private String resourceName;
    private Map<String, Object> metadata;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
