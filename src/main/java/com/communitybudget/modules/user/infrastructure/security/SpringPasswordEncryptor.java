package com.communitybudget.modules.user.infrastructure.security;

import com.communitybudget.modules.user.domain.service.PasswordEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class SpringPasswordEncryptor implements PasswordEncryptor {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordEncryptor(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(final String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}

