package xyz.yann.rbac.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.yann.rbac.demo.domain.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    boolean existsByRoleCode(String roleCode);
}
