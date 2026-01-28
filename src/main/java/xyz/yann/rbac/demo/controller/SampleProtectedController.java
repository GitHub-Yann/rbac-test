package xyz.yann.rbac.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.yann.rbac.demo.security.AuthorizationFacade;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
public class SampleProtectedController {

    private final AuthorizationFacade authorizationFacade;

    public SampleProtectedController(AuthorizationFacade authorizationFacade) {
        this.authorizationFacade = authorizationFacade;
    }

    @GetMapping("/build")
    public Map<String, Object> triggerBuild(@RequestParam Long principalId,
                                            @RequestParam String resourceKey) {
        authorizationFacade.checkPermission(principalId, resourceKey, "build");
        return Map.of("principalId", principalId, "resourceKey", resourceKey, "status", "BUILD_TRIGGERED");
    }
    @GetMapping("/deploy")
    public Map<String, Object> triggerDeploy(@RequestParam Long principalId,
                                            @RequestParam String resourceKey) {
        authorizationFacade.checkPermission(principalId, resourceKey, "deploy");
        return Map.of("principalId", principalId, "resourceKey", resourceKey, "status", "DEPLOY_TRIGGERED");
    }
}
