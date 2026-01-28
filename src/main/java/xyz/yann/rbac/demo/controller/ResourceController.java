package xyz.yann.rbac.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.yann.rbac.demo.controller.mapper.DtoMapper;
import xyz.yann.rbac.demo.dto.CreateResourceRequest;
import xyz.yann.rbac.demo.dto.ResourceResponse;
import xyz.yann.rbac.demo.service.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceResponse> createResources(@RequestBody @Valid List<CreateResourceRequest> requests) {
        return resourceService.createResources(requests)
                .stream()
                .map(DtoMapper::toResourceResponse)
                .toList();
    }

    @GetMapping
    public List<ResourceResponse> listResources() {
        return resourceService.listResources()
                .stream()
                .map(DtoMapper::toResourceResponse)
                .toList();
    }
}
