package xyz.yann.rbac.demo.security;

import org.springframework.stereotype.Component;
import xyz.yann.rbac.demo.dto.DecisionResponse;
import xyz.yann.rbac.demo.exception.AccessDeniedException;

@Component
public class AuthorizationFacade {

    private final DecisionService decisionService;

    public AuthorizationFacade(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    public void checkPermission(Long principalId, String resourceKey, String actionCode) {
        DecisionResponse response = decisionService.decide(principalId, resourceKey, actionCode);
        if (!response.isAllowed()) {
            throw new AccessDeniedException(response.getMessage());
        }
    }
}
