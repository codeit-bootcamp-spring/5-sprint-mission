package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // === CSRF 설정 ===
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        // === 인가 설정 ===
        .authorizeHttpRequests(auth -> auth
            // 인증 없이 접근 가능한 엔드포인트
            .requestMatchers(
                "/api/auth/login",       // 로그인
                "/api/auth/logout",      // 로그아웃
                "/api/auth/csrf-token",  // CSRF 토큰 발급
                "/api/users"             // 회원가입
            ).permitAll()
            // Swagger / Actuator 등 API가 아닌 요청
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**"
            ).permitAll()
            // 나머지는 모두 인증 필요
            .anyRequest().authenticated()
        )
        // === 로그인(formLogin) 설정 ===
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        // === 로그아웃 설정 ===
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            // LogoutSuccessHandler는 따로 구현해둔 것이 있으면 거기에 맞게 주입
            .logoutSuccessHandler(
                (request, response, authentication) -> {
                  response.setStatus(HttpStatus.NO_CONTENT.value()); // 204
                }
            )
        )
        // === 인증/인가 예외 처리 ===
        .exceptionHandling(ex -> ex
            // 인증 안 된 경우 → 401
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            // 권한 부족(인가 실패) → 403
            .accessDeniedHandler(new AccessDeniedHandlerImpl())
        )
        // === HTTP Basic 비활성화 (REST API 스타일) ===
        .httpBasic(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Role 계층 구조 설정: ADMIN > CHANNEL_MANAGER > USER
  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("""
        ROLE_ADMIN > ROLE_CHANNEL_MANAGER
        ROLE_CHANNEL_MANAGER > ROLE_USER
        """);
    return hierarchy;
  }

  // Method Security에서 RoleHierarchy 사용하도록 설정
  @Bean
  public static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy
  ) {
    DefaultMethodSecurityExpressionHandler handler =
        new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }
}