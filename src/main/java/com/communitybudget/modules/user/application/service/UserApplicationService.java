package com.communitybudget.modules.user.application.service;

import com.communitybudget.application.dto.UserCreateDTO;
import com.communitybudget.application.dto.UserDTO;
import com.communitybudget.application.dto.UserUpdateDTO;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.user.application.mapper.UserMapper;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserApplicationService implements UserDetailsService {

    private final UserService userService;

    public UserApplicationService(final UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public void registerUser(final UserCreateDTO userDTO) {
        User user = User.builder()
                .id(null)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .provider("local")
                .providerId(null)
                .createdAt(null)
                .build();

        userService.save(user);
    }

    @Transactional
    public void updateUser(final String email, final UserUpdateDTO userDTO) {
        User existingUser = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(existingUser.getPassword())
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

    public void deleteUser(final String email) {
        User existingUser = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userService.delete(existingUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
