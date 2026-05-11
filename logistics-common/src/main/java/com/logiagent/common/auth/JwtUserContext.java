package com.logiagent.common.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JwtUserContext {

    private Long userId;
    private String username;
    private List<String> roles = new ArrayList<>();
    private LocalDateTime expireAt;

    public JwtUserContext() {
    }

    public JwtUserContext(Long userId, String username, List<String> roles, LocalDateTime expireAt) {
        this.userId = userId;
        this.username = username;
        this.roles = roles == null ? new ArrayList<>() : new ArrayList<>(roles);
        this.expireAt = expireAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles == null ? new ArrayList<>() : new ArrayList<>(roles);
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
