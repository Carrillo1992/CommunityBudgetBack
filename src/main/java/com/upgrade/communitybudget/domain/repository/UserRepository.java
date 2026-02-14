package com.upgrade.communitybudget.domain.repository;

import com.upgrade.communitybudget.domain.model.User;

import java.util.Optional;

public interface UserRepository{

    Optional<User> findById(final Long id);

    void save(final User user);

    void update(final User user);

    void delete(final User user);

}

