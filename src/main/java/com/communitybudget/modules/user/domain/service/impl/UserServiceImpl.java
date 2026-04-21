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
    public void save(final User user) {
        // Validar que el email no exista antes de intentar guardar
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
                .password(passwordEncryptor.encode(user.getPassword()))
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .roles(roles)
                .build();
        userRepository.save(userWithEncodedPassword);
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

    @Override
    public void changePasswordByUserId(final Long userId, final String newPassword) {
        // Buscar el usuario por ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Crear usuario actualizado con la nueva contraseña encriptada
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
}
