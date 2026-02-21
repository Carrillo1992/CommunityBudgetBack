package com.communitybudget.modules.user.infrastructure.persistence.common;

import com.communitybudget.modules.user.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSpringRoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(final String name);
}