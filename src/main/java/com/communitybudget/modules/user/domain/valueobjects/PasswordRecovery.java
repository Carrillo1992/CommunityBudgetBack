package com.communitybudget.modules.user.domain.valueobjects;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PasswordRecovery {

    private String token;
    private String email;
    private LocalDateTime expTime;

    public PasswordRecovery(final String token, final String email, final LocalDateTime expTime) {
        this.token = token;
        this.email = email;
        this.expTime = expTime;
    }

}
