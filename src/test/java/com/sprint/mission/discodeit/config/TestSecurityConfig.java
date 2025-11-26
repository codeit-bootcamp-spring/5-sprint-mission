package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.mockito.Mockito.mock;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role(Role.ADMIN.name())
            .implies(Role.USER.name(), Role.CHANNEL_MANAGER.name())
            .role(Role.CHANNEL_MANAGER.name())
            .implies(Role.USER.name())
            .build();
    }

    @Bean
    public JwtRegistry jwtRegistry() {
        return new NoOpJwtRegistry();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }

    private static class NoOpJwtRegistry implements JwtRegistry {

        @Override
        public void registerJwtInformation(JwtInformation jwtInformation) {
        }

        @Override
        public void invalidateJwtInformationByUserId(UUID userId) {
        }

        @Override
        public boolean hasActiveJwtInformationByUserId(UUID userId) {
            return false;
        }

        @Override
        public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
            return false;
        }

        @Override
        public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
            return false;
        }

        @Override
        public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        }

        @Override
        public void clearExpiredJwtInformation() {
        }
    }
}
