package com.upgrade.communitybudget.infrastructure.persistence.common;

import com.upgrade.communitybudget.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSpringUserRepository extends JpaRepository<UserEntity, Long> {
}
