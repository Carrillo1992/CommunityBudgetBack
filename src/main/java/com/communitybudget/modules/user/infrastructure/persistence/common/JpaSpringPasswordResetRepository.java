package com.communitybudget.modules.user.infrastructure.persistence.common;

import com.communitybudget.modules.user.infrastructure.persistence.entity.PasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSpringPasswordResetRepository extends JpaRepository<PasswordResetEntity, Long> {

    void deleteByEmail(final String email);

    Optional<PasswordResetEntity> findByEmail(final String email);

    Optional<PasswordResetEntity> findByToken(final String token);

}
