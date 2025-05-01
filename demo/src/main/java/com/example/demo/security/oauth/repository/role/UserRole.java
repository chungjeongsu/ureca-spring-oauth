package com.example.demo.security.oauth.repository.role;

public enum UserRole {
    ROLE_TESTER("ROLE_TESTER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_OWNER("ROLE_OWNER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
