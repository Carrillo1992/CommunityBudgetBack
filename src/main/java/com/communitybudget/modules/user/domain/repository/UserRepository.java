package com.communitybudget.modules.user.domain.repository;

import com.communitybudget.modules.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(final Long id);

    Optional<User> findByEmail(final String email);

    void save(final User user);

    void update(final User user);

    void delete(final User user);


}

