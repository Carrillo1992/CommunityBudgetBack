package com.communitybudget.modules.user.application.service;


import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.common.exceptions.exception.ConflictException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.user.application.dto.UserCreateDTO;
import com.communitybudget.modules.user.application.dto.UserDTO;
import com.communitybudget.modules.user.application.dto.UserUpdateDTO;
import com.communitybudget.modules.user.application.mapper.UserMapper;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.valueobjects.RoleValue;
import com.communitybudget.modules.user.application.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserApplicationService implements UserDetailsService {

    private final UserService userService;

    public UserApplicationService(final UserService userService) {
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO registerUser(final UserCreateDTO userDTO) {
        User user = User.builder()
                .id(null)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .provider("local")
                .providerId(null)
                .createdAt(null)
                .build();

        User savedUser = userService.save(user);
        return UserMapper.INSTANCE.toDto(savedUser);
    }

    @Transactional
    public void updateUser(final String email, final UserUpdateDTO userDTO) {
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(email)
                && userService.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ConflictException("User with email " + userDTO.getEmail() + " already exists");
        }
        User existingUser = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        User updatedUser = User.builder()
                .id(existingUser.getId())
                .name(userDTO.getName() != null ? userDTO.getName() : existingUser.getName())
                .email(userDTO.getEmail() != null ? userDTO.getEmail() : existingUser.getEmail())
                .password(existingUser.getPassword())
                .avatarUrl(userDTO.getAvatarUrl() != null ? userDTO.getAvatarUrl() : existingUser.getAvatarUrl())
                .provider(existingUser.getProvider())
                .providerId(existingUser.getProviderId())
                .createdAt(existingUser.getCreatedAt())
                .build();

        userService.update(updatedUser);
    }

    @Transactional
    public void changePassword(final String email, final String currentPassword, final String newPassword) {
        User existingUser = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userService.changePassword(existingUser, currentPassword, newPassword);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(final String email) {
        return userService.findByEmail(email)
                .map(UserMapper.INSTANCE::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void deleteUser(final String email) {
        User existingUser = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userService.delete(existingUser);
    }

    @Transactional
    public void deleteUserById(final Long id, final String currentUserEmail) {
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (existingUser.getEmail().equals(currentUserEmail)) {
            throw new BadRequestException("Cannot delete your own account.");
        }

        userService.delete(existingUser);
    }

    @Transactional
    public void promoteToAdmin(final Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userService.addAdminRole(user);
    }

    @Transactional
    public void revokeAdmin(final Long userId, final String currentUserEmail) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getEmail().equals(currentUserEmail)) {
            throw new BadRequestException("Cannot revoke your own admin role");
        }

        userService.removeAdminRole(user);
    }

    @Transactional
    public void changePasswordByUserId(final Long userId, final String newPassword) {
        userService.changePasswordByUserId(userId, newPassword);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(final Long id) {
        return userService.findById(id)
                .map(UserMapper.INSTANCE::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void updateUserById(final Long id, final UserUpdateDTO userDTO) {
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .name(userDTO.getName() != null ? userDTO.getName() : existingUser.getName())
                .email(userDTO.getEmail() != null ? userDTO.getEmail() : existingUser.getEmail())
                .password(existingUser.getPassword())
                .avatarUrl(userDTO.getAvatarUrl() != null ? userDTO.getAvatarUrl() : existingUser.getAvatarUrl())
                .provider(existingUser.getProvider())
                .providerId(existingUser.getProviderId())
                .roles(existingUser.getRoles())
                .createdAt(existingUser.getCreatedAt())
                .build();

        userService.update(updatedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        List<SimpleGrantedAuthority> authorities = user.getRoles() != null
                ? user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList())
                : List.of(new SimpleGrantedAuthority(RoleValue.USER.getValue()));

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
