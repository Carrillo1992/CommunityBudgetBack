package com.communitybudget.modules.user.infrastructure.web;

import com.communitybudget.config.security.JwtUtils;
import com.communitybudget.modules.user.application.dto.LoginResponseDTO;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import com.communitybudget.modules.user.domain.valueobjects.RoleValue;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/oauth")
public class GoogleOAuthController {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthController.class);

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private GoogleIdTokenVerifier verifier;

    public GoogleOAuthController(final UserService userService, final JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostConstruct
    public void init() {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> googleLogin(@Valid @RequestBody Map<String, String> request) {
        try {
            GoogleIdToken idToken = verifier.verify(request.get("idToken"));
            if (idToken == null) {
                log.warn("El token de Google es inválido o nulo");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String providerId = payload.getSubject();
            String pictureUrl = (String) payload.get("picture");

            User user = userService.findByEmail(email).orElse(null);

            if (user != null) {
                // El usuario ya existe. Vinculamos su cuenta de Google si no lo estaba.
                if (user.getProvider() == null || !user.getProvider().equals("google")) {
                    user = User.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .password(user.getPassword())
                            .avatarUrl(user.getAvatarUrl())
                            .provider("google")
                            .providerId(providerId)
                            .roles(user.getRoles())
                            .createdAt(user.getCreatedAt())
                            .build();
                    user = userService.updateUser(user);
                }
            } else {
                User newUser = User.builder()
                        .name(name)
                        .email(email)
                        .password(null)
                        .provider("google")
                        .providerId(providerId)
                        .avatarUrl(pictureUrl)
                        .build();
                user = userService.save(newUser);
            }

            List<SimpleGrantedAuthority> authorities = user.getRoles() != null
                    ? user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList())
                    : List.of(new SimpleGrantedAuthority(RoleValue.USER.getValue()));

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password("")
                    .authorities(authorities)
                    .build();

            String accessToken = jwtUtils.generateToken(userDetails, user.getId());
            String refreshToken = jwtUtils.generateRefreshToken(email);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(jwtUtils.getAccessTokenExpirationSeconds());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error durante la autenticación con Google", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
