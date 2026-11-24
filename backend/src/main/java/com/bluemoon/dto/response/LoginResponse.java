package com.bluemoon.dto.response;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long accountId;
    private String username;
    private String role;
    private String fullName;

    public LoginResponse() {}

    public LoginResponse(String token, Long accountId, String username, String role, String fullName) {
        this.token = token;
        this.tokenType = "Bearer";
        this.accountId = accountId;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
