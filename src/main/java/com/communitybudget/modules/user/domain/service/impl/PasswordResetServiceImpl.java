package com.communitybudget.modules.user.domain.service.impl;

import com.communitybudget.common.utils.StringUtils;
import com.communitybudget.modules.user.domain.exception.InvalidTokenException;
import com.communitybudget.modules.user.domain.repository.PasswordResetRepository;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.domain.service.PasswordResetService;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.valueobjects.PasswordRecovery;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final UserService userService;

    public PasswordResetServiceImpl(final UserRepository userRepository, final PasswordResetRepository passwordResetRepository, final UserService userService) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void tokenValidation(final String token, final String newPassword) {
        PasswordRecovery resetPassword = passwordResetRepository.findByToken(token);
        if (resetPassword == null || !isValidToken(resetPassword)) {
            throw new InvalidTokenException("Invalid token provided");
        }
        userService.changePassword(newPassword, resetPassword.getEmail());
        passwordResetRepository.deleteByEmail(resetPassword.getEmail());
    }

    @Override
    @Transactional
    public String createToken(final String email) {
        if (userRepository.existsByEmail(email)) {
            String token = createNewToken();
            LocalDateTime expTime = LocalDateTime.now().plusMinutes(15);
            PasswordRecovery recovery = PasswordRecovery.builder()
                    .token(token)
                    .email(email)
                    .expTime(expTime)
                    .build();
            passwordResetRepository.save(recovery);
            return token;
        }
        return null;
    }

    private boolean isValidToken(final PasswordRecovery resetPassword) {
        return resetPassword.getExpTime().isAfter(LocalDateTime.now());
    }

    private String createNewToken() {
        return StringUtils.generateRandomString(6);
    }
}
