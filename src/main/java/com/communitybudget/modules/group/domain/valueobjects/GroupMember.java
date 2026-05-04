package com.communitybudget.modules.group.domain.valueobjects;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupMember {

    private final Long id;
    private final Long userId;
    private Boolean isAdmin;
    private final LocalDateTime joinedAt;


    public void promoteToAdmin() {
        if (this.isAdmin) {
            throw new IllegalStateException("User is already an admin");
        }
        this.isAdmin = true;
    }
}
