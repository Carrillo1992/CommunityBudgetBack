package com.upgrade.communitybudget.infrastructure.persistence;

import com.upgrade.communitybudget.config.exceptions.exception.ResourceNotFoundException;
import com.upgrade.communitybudget.domain.model.User;
import com.upgrade.communitybudget.domain.repository.UserRepository;
import com.upgrade.communitybudget.infrastructure.mapper.UserMapper;
import com.upgrade.communitybudget.infrastructure.persistence.common.JpaSpringUserRepository;
import com.upgrade.communitybudget.infrastructure.persistence.entity.UserEntity;
import jakarta.transaction.Transactional;

import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    public final JpaSpringUserRepository repository;

    public UserRepositoryImpl(JpaSpringUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findById(final Long id) {
        return repository.findById(id)
                .map(UserMapper.INSTANCE::toDomain)
                .or(() ->{
                    throw new ResourceNotFoundException("User not found with id: " + id);
                });
    }

    @Override
    public void save(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        repository.save(entity);
    }

    @Override
    public void update(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        repository.findById(entity.getId())
                .ifPresent(repository::save);

    }

    @Transactional
    @Override
    public void delete(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        repository.findById(entity.getId())
                .ifPresent(repository::delete);
    }
}
