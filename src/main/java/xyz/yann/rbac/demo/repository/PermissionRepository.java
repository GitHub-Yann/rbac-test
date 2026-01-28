package xyz.yann.rbac.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xyz.yann.rbac.demo.domain.PermissionEntity;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByResourceIdAndActionCode(Long resourceId, String actionCode);

    List<PermissionEntity> findByResourceId(Long resourceId);
}
