package com.communitybudget.modules.user.domain.service.impl;

import com.communitybudget.modules.user.domain.exception.InvalidPasswordException;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.domain.service.PasswordEncryptor;
import com.communitybudget.modules.user.domain.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncryptor passwordEncryptor;

    public UserServiceImpl(final UserRepository repository, final PasswordEncryptor passwordEncryptor) {
        this.repository = repository;
        this.passwordEncryptor = passwordEncryptor;
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void save(final User user) {
        User userWithEncodedPassword = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncryptor.encode(user.getPassword()))
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .build();
        repository.save(userWithEncodedPassword);
    }

    @Override
    public void update(final User user) {
        repository.update(user);
    }

    @Override
    public void delete(final User user) {
        repository.delete(user);
    }

    @Override
    public void changePassword(final User user, final String currentPassword, final String newPassword) {

        if (!passwordEncryptor.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (passwordEncryptor.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncryptor.encode(newPassword))
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .build();

        repository.update(updatedUser);
    }
}
