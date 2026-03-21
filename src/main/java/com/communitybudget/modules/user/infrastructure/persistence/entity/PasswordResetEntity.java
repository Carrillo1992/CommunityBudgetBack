package com.communitybudget.modules.user.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_resets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordResetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
