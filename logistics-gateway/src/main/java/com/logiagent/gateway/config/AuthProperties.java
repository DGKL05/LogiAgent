package com.logiagent.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private List<String> whitelist = new ArrayList<>(List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/internal/health",
            "/actuator/**"
    ));
    private List<String> adminOnly = new ArrayList<>(List.of(
            "/api/admin/**",
            "/api/dispatch/**"
    ));

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist == null ? new ArrayList<>() : new ArrayList<>(whitelist);
    }

    public List<String> getAdminOnly() {
        return adminOnly;
    }

    public void setAdminOnly(List<String> adminOnly) {
        this.adminOnly = adminOnly == null ? new ArrayList<>() : new ArrayList<>(adminOnly);
    }
}
