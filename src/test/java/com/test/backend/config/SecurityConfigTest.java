package com.test.backend.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class SecurityConfigTest {

    @Test
    void keepsUserAndAdminAuthoritiesSeparateWhenDistinctCredentialsAreConfigured() {
        SecurityProperties properties = new SecurityProperties();
        properties.setUsername("visitor-user");
        properties.setPassword("user-password");
        properties.setAdminUsername("site-admin");
        properties.setAdminPassword("admin-password");
        SecurityConfig config = new SecurityConfig(properties);
        UserDetailsService users = config.userDetailsService(new BCryptPasswordEncoder());

        var user = users.loadUserByUsername("visitor-user");
        var admin = users.loadUserByUsername("site-admin");

        assertTrue(user.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_USER")));
        assertFalse(user.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(admin.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")));
        assertFalse(admin.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_USER")));
    }
}
