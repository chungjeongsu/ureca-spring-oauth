package com.example.demo.domain.entity.role;

public enum UserRole {
    ROLE_TESTER("ROLE_TESTER"),
    ROLE_ANONYMOUS("ROLE_ANONYMOUS"),
    ROLE_OWNER("ROLE_OWNER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
