package com.communitybudget.modules.user.infrastructure.web;


import com.communitybudget.modules.user.application.dto.ChangePasswordDTO;
import com.communitybudget.modules.user.application.dto.UserDTO;
import com.communitybudget.modules.user.application.dto.UserUpdateDTO;
import com.communitybudget.modules.user.application.service.UserApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();

        return ResponseEntity.status(HttpStatus.OK).body(userApplicationService.getUserByEmail(userEmail));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateUser(final Authentication authentication,@RequestBody final UserUpdateDTO userDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String userEmail = authentication.getName();
        userApplicationService.updateUser(userEmail, userDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(final Authentication authentication, @RequestBody final ChangePasswordDTO changePasswordDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String userEmail = authentication.getName();

        userApplicationService.changePassword(userEmail, changePasswordDTO.getCurrentPassword(), changePasswordDTO.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String userEmail = authentication.getName();
        userApplicationService.deleteUser(userEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userApplicationService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userApplicationService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUserById(@PathVariable Long id, @RequestBody UserUpdateDTO userDTO) {
        userApplicationService.updateUserById(id, userDTO);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id, Authentication authentication) {
        userApplicationService.deleteUserById(id, authentication.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/promote-admin")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable Long id) {
        userApplicationService.promoteToAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/revoke-admin")
    public ResponseEntity<Void> revokeAdmin(@PathVariable Long id, Authentication authentication) {
        userApplicationService.revokeAdmin(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

}
