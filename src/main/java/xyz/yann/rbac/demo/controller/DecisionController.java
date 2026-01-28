package xyz.yann.rbac.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.yann.rbac.demo.dto.DecisionRequest;
import xyz.yann.rbac.demo.dto.DecisionResponse;
import xyz.yann.rbac.demo.security.DecisionService;

@RestController
@RequestMapping("/api/v1/decision")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public DecisionResponse decide(@RequestBody @Valid DecisionRequest request) {
        return decisionService.decide(request.getPrincipalId(), request.getResourceKey(), request.getActionCode());
    }
}
