package xyz.yann.rbac.demo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.yann.rbac.demo.domain.MemberRoleEntity;

import java.util.List;
import java.util.Optional;

public interface MemberRoleRepository extends JpaRepository<MemberRoleEntity, Long> {

    @EntityGraph(attributePaths = "role")
    Optional<MemberRoleEntity> findByMemberIdAndRole_Id(Long memberId, Long roleId);

    @EntityGraph(attributePaths = "role")
    List<MemberRoleEntity> findByMemberId(Long memberId);

    @Override
    @EntityGraph(attributePaths = "role")
    List<MemberRoleEntity> findAll();
}
