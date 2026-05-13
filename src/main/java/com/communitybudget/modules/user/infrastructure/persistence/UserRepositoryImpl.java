package com.communitybudget.modules.user.infrastructure.persistence;

import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.infrastructure.mapper.UserMapper;
import com.communitybudget.modules.user.infrastructure.persistence.common.JpaSpringUserRepository;
import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaSpringUserRepository userRepository;

    public UserRepositoryImpl(JpaSpringUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll()
                .stream().map(UserMapper.INSTANCE::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findById(final Long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toDomain);
    }

    @Override
    public List<User> findAllById(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream().map(UserMapper.INSTANCE::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper.INSTANCE::toDomain);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return UserMapper.INSTANCE.toDomain(savedEntity);
    }

    @Override
    public void update(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        userRepository.findById(entity.getId())
                .ifPresent(existingEntity -> userRepository.save(entity));
    }

    @Override
    public void delete(final User user) {
        UserEntity entity = UserMapper.INSTANCE.toEntity(user);
        userRepository.findById(entity.getId())
                .ifPresent(userRepository::delete);
    }
}
