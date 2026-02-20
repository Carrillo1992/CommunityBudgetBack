package com.communitybudget.modules.user.domain.service;

import com.communitybudget.modules.user.domain.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(final String email);

    void save(final User user);

    void update(final User user);

    void delete(final User user);

    void changePassword(final User user, final String currentPassword, final String newPassword);

}
