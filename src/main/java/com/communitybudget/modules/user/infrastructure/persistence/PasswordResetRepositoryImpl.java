package com.communitybudget.modules.user.infrastructure.persistence;

import com.communitybudget.modules.user.domain.repository.PasswordResetRepository;
import com.communitybudget.modules.user.domain.valueobjects.PasswordRecovery;
import com.communitybudget.modules.user.infrastructure.mapper.PasswordResetMapper;
import com.communitybudget.modules.user.infrastructure.persistence.common.JpaSpringPasswordResetRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetRepositoryImpl implements PasswordResetRepository {

    private final JpaSpringPasswordResetRepository jpaSpringPasswordResetRepository;

    public PasswordResetRepositoryImpl(JpaSpringPasswordResetRepository jpaSpringPasswordResetRepository) {
        this.jpaSpringPasswordResetRepository = jpaSpringPasswordResetRepository;
    }

    @Override
    public PasswordRecovery findByToken(final String token) {
        return jpaSpringPasswordResetRepository.findByEmail(token)
                .map(PasswordResetMapper.INSTANCE::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteByEmail(final String email) {
        jpaSpringPasswordResetRepository.deleteByEmail(email);
    }

    @Override
    public void save(final PasswordRecovery passwordRecovery) {
        jpaSpringPasswordResetRepository.save(PasswordResetMapper.INSTANCE.toPersistence(passwordRecovery));
    }
}
