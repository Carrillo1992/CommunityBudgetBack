package com.communitybudget.modules.user.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {

    private Long id;
    private String name;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
