package xyz.yann.rbac.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.yann.rbac.demo.domain.ResourceEntity;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {

    boolean existsByResourceKey(String resourceKey);

    Optional<ResourceEntity> findByResourceKey(String resourceKey);
}
