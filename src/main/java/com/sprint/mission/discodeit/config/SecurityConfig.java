package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.security.LoginFailureHandler;
import com.sprint.mission.discodeit.config.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.config.security.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // 로그인 성공/실패 핸들러 주입
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
            .requestMatchers(
                "/api/auth/login",
                "/api/auth/csrf-token",
                "/api/users"
            ).permitAll()
            .anyRequest().authenticated()
        )
        // === 로그인(formLogin) 설정 ===
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            // 로그인 성공 시 UserDto를 반환하는 커스텀 성공 핸들러
            .successHandler(loginSuccessHandler)
            // 로그인 실패 시 401 ErrorResponse 반환하는 실패 핸들러
            .failureHandler(loginFailureHandler)
        )
        // === 로그아웃 설정 ===
        .logout(logout -> logout
            // 로그아웃 요청 URL (미션 요구사항: /api/auth/logout)
            .logoutUrl("/api/auth/logout")
            // HttpStatusReturningLogoutSuccessHandler 사용, 204 No Content 반환
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
            )
        )
        // === HTTP Basic 비활성화 (REST API 스타일) ===
        .httpBasic(AbstractHttpConfigurer::disable);

    return http.build();
  }
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}