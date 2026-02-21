package com.communitybudget.modules.user.domain.repository;


import com.communitybudget.modules.user.domain.model.Role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

}
