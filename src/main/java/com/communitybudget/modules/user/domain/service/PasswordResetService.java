package com.communitybudget.modules.user.domain.service;

public interface PasswordResetService {

    void tokenValidation(final String token, final String newPassword);

    String createToken(final String email);

}
