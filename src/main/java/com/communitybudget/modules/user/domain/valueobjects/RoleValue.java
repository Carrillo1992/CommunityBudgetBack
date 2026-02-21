package com.communitybudget.modules.user.domain.valueobjects;

import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;

import java.util.Arrays;

public enum RoleValue {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    final String value;

    RoleValue(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static RoleValue fromString(final String value) {
        return Arrays.stream(RoleValue.values())
                .filter(role -> role.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role not Found"));
    }
}
