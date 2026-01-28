package xyz.yann.rbac.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.yann.rbac.demo.domain.ResourceEntity;
import xyz.yann.rbac.demo.dto.CreateResourceRequest;
import xyz.yann.rbac.demo.repository.ResourceRepository;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    public ResourceEntity createResource(CreateResourceRequest request) {
        if (repository.existsByResourceKey(request.getResourceKey())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "resource key already exists");
        }
        ResourceEntity entity = new ResourceEntity();
        entity.setDomain(request.getDomain());
        entity.setType(request.getType());
        entity.setResourceKey(request.getResourceKey());
        entity.setResourceName(request.getResourceName());
        entity.setMetadata(request.getMetadata());
        return repository.save(entity);
    }

    public List<ResourceEntity> createResources(List<CreateResourceRequest> requests) {
        return requests.stream().map(this::createResource).toList();
    }

    public List<ResourceEntity> listResources() {
        return repository.findAll();
    }

    public ResourceEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "resource not found"));
    }

    public ResourceEntity getByResourceKey(String resourceKey) {
        return repository.findByResourceKey(resourceKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "resource not found"));
    }
}
