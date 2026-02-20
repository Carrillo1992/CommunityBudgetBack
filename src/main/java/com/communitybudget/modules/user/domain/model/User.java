package com.communitybudget.modules.user.domain.model;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
public class User {

    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final String provider;
    private final String providerId;
    private final LocalDateTime createdAt;

}
