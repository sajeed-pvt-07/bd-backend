package com.test.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Autowired
    public SecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        String username = securityProperties.getUsername();
        String password = securityProperties.getPassword();
        String adminUsername = securityProperties.getAdminUsername();
        String adminPassword = securityProperties.getAdminPassword();

        if (adminUsername.isBlank() != adminPassword.isBlank()) {
            throw new IllegalStateException("Configure both ADMIN_USERNAME and ADMIN_PASSWORD");
        }
        if (username.equals(adminUsername) && !password.equals(adminPassword)) {
            throw new IllegalStateException("Use separate usernames when configuring distinct user and admin passwords");
        }

        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles(username.equals(adminUsername) ? new String[] { "USER", "ADMIN" } : new String[] { "USER" })
                .build();
        if (adminUsername.isBlank() || username.equals(adminUsername)) {
            return new InMemoryUserDetailsManager(user);
        }

        UserDetails admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for this API
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/analytics/events").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/v1/config/**",
                        "/api/v1/user/**",
                        "/api/v1/greetings/**",
                        "/api/v1/timeline-items/**",
                        "/api/v1/songs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,
                        "/api/v1/user/**",
                        "/api/v1/greetings/**",
                        "/api/v1/timeline-items/**",
                        "/api/v1/songs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,
                        "/api/v1/config/**",
                        "/api/v1/user/**",
                        "/api/v1/greetings/**",
                        "/api/v1/timeline-items/**",
                        "/api/v1/songs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {
                // default settings
            });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
