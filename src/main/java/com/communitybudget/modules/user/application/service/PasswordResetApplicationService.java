package com.communitybudget.modules.user.application.service;

import com.communitybudget.modules.user.domain.service.EmailService;
import com.communitybudget.modules.user.domain.service.PasswordResetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetApplicationService {

    private static final String URL_RESET_PASSWORD = "/api/v1/email/reset-password";

    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public PasswordResetApplicationService(final PasswordResetService passwordResetService, final EmailService emailService) {
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @Transactional
    public void processPasswordReset(final String email) {
        String token = passwordResetService.createToken(email);
        if (token != null) {
            final String resetLink = URL_RESET_PASSWORD + "/" + token;
            emailService.sendPasswordResetEmail(email, resetLink);
        }
    }

    public void processTokenValidation(final String token, final String newPassword) {
        passwordResetService.tokenValidation(token, newPassword);
    }
}
