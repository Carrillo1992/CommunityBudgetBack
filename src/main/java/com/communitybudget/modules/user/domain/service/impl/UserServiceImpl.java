package com.communitybudget.modules.user.domain.service.impl;

import com.communitybudget.common.exceptions.exception.ConflictException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.user.domain.exception.InvalidPasswordException;
import com.communitybudget.modules.user.domain.model.Role;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.repository.RoleRepository;
import com.communitybudget.modules.user.domain.repository.UserRepository;
import com.communitybudget.modules.user.domain.service.PasswordEncryptor;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.valueobjects.RoleValue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncryptor passwordEncryptor;

    public UserServiceImpl(final UserRepository userRepository, final PasswordEncryptor passwordEncryptor, final RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncryptor = passwordEncryptor;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(final Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(final User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + user.getEmail());
        }

        final Set<Role> roles = user.getRoles() == null || user.getRoles().isEmpty() ?
                Set.of(roleRepository.findByName(RoleValue.USER.getValue())
                        .orElseThrow(() -> new IllegalArgumentException("Role USER not found"))) :
                (user.getRoles());

        User userWithEncodedPassword = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword() != null ? passwordEncryptor.encode(user.getPassword()) : null)
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .roles(roles)
                .build();
        return userRepository.save(userWithEncodedPassword);
    }

    @Override
    public User updateUser(User user){
        return userRepository.save(user);
    }

    @Override
    public void update(final User user) {
        userRepository.update(user);
    }

    @Override
    public void delete(final User user) {
        userRepository.delete(user);
    }

    @Override
    public void changePassword(final User user, final String currentPassword, final String newPassword) {

        if (!passwordEncryptor.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (passwordEncryptor.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncryptor.encode(newPassword))
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();

        userRepository.update(updatedUser);
    }

    @Override
    public void changePassword(final String newPassword, final String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncryptor.encode(newPassword))
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();

    }

    @Override
    public void addAdminRole(final User user) {
        Role adminRole = roleRepository.findByName(RoleValue.ADMIN.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Role ADMIN not found"));

        Set<Role> updatedRoles = new HashSet<>(user.getRoles() != null ? user.getRoles() : Set.of());
        updatedRoles.add(adminRole);

        User updatedUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .roles(updatedRoles)
                .build();

        userRepository.update(updatedUser);
    }

    @Override
    public void removeAdminRole(final User user) {
        Set<Role> updatedRoles = new HashSet<>(user.getRoles() != null ? user.getRoles() : Set.of());
        updatedRoles.removeIf(role -> RoleValue.ADMIN.getValue().equals(role.getName()));

        // Asegurar que al menos tenga el rol USER
        if (updatedRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(RoleValue.USER.getValue())
                    .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));
            updatedRoles.add(userRole);
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .roles(updatedRoles)
                .build();

        userRepository.update(updatedUser);
    }
}
