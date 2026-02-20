package com.communitybudget.modules.user.infrastructure.web;

import com.communitybudget.application.dto.ChangePasswordDTO;
import com.communitybudget.application.dto.UserDTO;
import com.communitybudget.application.dto.UserUpdateDTO;
import com.communitybudget.modules.user.application.service.UserApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

}
