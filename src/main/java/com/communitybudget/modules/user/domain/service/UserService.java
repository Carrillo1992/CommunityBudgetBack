package com.communitybudget.modules.user.domain.service;

import com.communitybudget.modules.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(final Long id);

    Optional<User> findByEmail(final String email);

    User save(final User user);

    void update(final User user);

    User updateUser(User user);

    void delete(final User user);

    void changePassword(final User user, final String currentPassword, final String newPassword);

    void changePassword(final String newPassword, final String email);

    void addAdminRole(final User user);

    void removeAdminRole(final User user);

}
