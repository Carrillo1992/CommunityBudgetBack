package com.communitybudget.modules.user.infrastructure.web;

import com.communitybudget.application.dto.*;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.config.security.JwtUtils;
import com.communitybudget.modules.user.application.mapper.UserMapper;
import com.communitybudget.modules.user.application.service.PasswordResetApplicationService;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.valueobjects.RoleValue;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetApplicationService passwordResetService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final String BEARER_PREFIX = "Bearer";

    public AuthController(final UserService userService,final  PasswordResetApplicationService passwordResetService, final AuthenticationManager authenticationManager, final JwtUtils jwtUtils) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody final UserCreateDTO userCreateDTO) {
        User savedUser = userService.save(UserMapper.INSTANCE.fromCreateDto(userCreateDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.INSTANCE.toDto(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody final LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtils.generateToken(userDetails, user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtils.getAccessTokenExpirationSeconds());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody final RefreshTokenRequestDTO refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtils.extractUsername(refreshToken);
        User user = userService.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = user.getRoles() != null
                ? user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList())
                : List.of(new SimpleGrantedAuthority(RoleValue.USER.getValue()));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();

        String newAccessToken = jwtUtils.generateToken(userDetails, user.getId());
        String newRefreshToken = jwtUtils.generateRefreshToken(username);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType(BEARER_PREFIX);
        response.setExpiresIn(jwtUtils.getAccessTokenExpirationSeconds());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") final String email) {
       passwordResetService.processPasswordReset(email);
       return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestDTO request){
        passwordResetService.processTokenValidation(request.getToken(), request.getPassword());
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
