package com.communitybudget.modules.user.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
public class User {

    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final String provider;
    private final String providerId;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;

}
