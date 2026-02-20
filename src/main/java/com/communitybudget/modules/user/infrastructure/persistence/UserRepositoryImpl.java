package com.communitybudget.modules.user.infrastructure.persistence;

import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.infrastructure.mapper.UserMapper;
import com.communitybudget.modules.user.infrastructure.persistence.common.JpaSpringUserRepository;
import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    public final JpaSpringUserRepository repository;

    public UserRepositoryImpl(JpaSpringUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findById(final Long id) {
        return repository.findById(id)
                .map(UserMapper.INSTANCE::toDomain);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return repository.findByEmail(email)
                .map(UserMapper.INSTANCE::toDomain);
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
                .ifPresent(existingEntity -> repository.save(entity));
    }

    @Transactional
    @Override
    public void delete(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        repository.findById(entity.getId())
                .ifPresent(repository::delete);
    }
}
