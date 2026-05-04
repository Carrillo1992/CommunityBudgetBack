package com.communitybudget.modules.user.domain.repository;

import com.communitybudget.modules.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(final Long id);

    Optional<User> findByEmail(final String email);

    Boolean existsByEmail(final String email);

    User save(final User user);

    void update(final User user);

    void delete(final User user);


}
