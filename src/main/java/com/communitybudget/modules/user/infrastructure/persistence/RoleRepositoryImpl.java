package com.communitybudget.modules.user.infrastructure.persistence;

import com.communitybudget.modules.user.domain.model.Role;
import com.communitybudget.modules.user.domain.repository.RoleRepository;
import com.communitybudget.modules.user.infrastructure.mapper.RoleMapper;
import com.communitybudget.modules.user.infrastructure.persistence.common.JpaSpringRoleRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final JpaSpringRoleRepository jpaSpringRoleRepository;

    public RoleRepositoryImpl(final JpaSpringRoleRepository jpaSpringRoleRepository) {
        this.jpaSpringRoleRepository = jpaSpringRoleRepository;
    }

    @Override
    public Optional<Role> findById(final Long id) {
        return jpaSpringRoleRepository.findById(id)
                .map(RoleMapper.INSTANCE::toDomain);
    }

    @Override
    public Optional<Role> findByName(final String name) {
        return jpaSpringRoleRepository.findByName(name)
                .map(RoleMapper.INSTANCE::toDomain);
    }
}
