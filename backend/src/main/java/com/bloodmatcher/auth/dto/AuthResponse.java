package com.bloodmatcher.auth.dto;

import com.bloodmatcher.user.dto.UserDto;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private String refreshToken;
    private UserDto user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, long expiresInMs, String refreshToken, UserDto user) {
        this.accessToken = accessToken;
        this.expiresInMs = expiresInMs;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
