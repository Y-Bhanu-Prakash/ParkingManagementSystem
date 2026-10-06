package com.parking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // REST API does not use browser sessions/forms.
                .csrf(csrf -> csrf.disable())

                // Authentication is stateless.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Return HTTP 401 for unauthenticated requests.
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )

                // Authorization rules.
                .authorizeHttpRequests(authorize -> authorize

                        // Login is publicly accessible.
                        .requestMatchers("/api/auth/login").permitAll()

                        // OWNER-only endpoints.
                        .requestMatchers("/api/owner/**")
                        .hasRole("OWNER")

                        // OWNER + ATTENDANT endpoints.
                        .requestMatchers("/api/attendant/**")
                        .hasAnyRole("OWNER", "ATTENDANT")

                        // Everything else requires authentication.
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}