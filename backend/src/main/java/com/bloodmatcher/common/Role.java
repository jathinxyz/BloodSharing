package com.bloodmatcher.common;

public enum Role {
    DONOR,
    RECIPIENT,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
