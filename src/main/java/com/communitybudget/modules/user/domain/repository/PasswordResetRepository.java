package com.communitybudget.modules.user.domain.repository;

import com.communitybudget.modules.user.domain.valueobjects.PasswordRecovery;

public interface PasswordResetRepository {

    PasswordRecovery findByToken(final String token);

    void deleteByEmail(final String email);

    void save(final PasswordRecovery passwordRecovery);

}
