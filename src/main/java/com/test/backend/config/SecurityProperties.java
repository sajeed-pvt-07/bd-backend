package com.test.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Holds security-related properties (username/password) read from application configuration
 * (app.security.username and app.security.password). This is injected into SecurityConfig.
 */
@Component
public class SecurityProperties {

    @Value("${app.security.username:admin}")
    private String username;

    @Value("${app.security.password:password}")
    private String password;

    @Value("${app.security.admin-username:${app.security.username:admin}}")
    private String adminUsername;

    @Value("${app.security.admin-password:${app.security.password:password}}")
    private String adminPassword;

    public SecurityProperties() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
