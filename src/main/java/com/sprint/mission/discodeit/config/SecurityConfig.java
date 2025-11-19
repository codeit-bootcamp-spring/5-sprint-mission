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
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        // === 로그아웃 설정 ===
        .logout(logout -> logout
            // 로그아웃 요청 URL (기본은 /logout 이지만 미션 요구사항에 맞게 변경)
            .logoutUrl("/api/auth/logout")
            // 204 No Content 응답을 반환하는 LogoutSuccessHandler로 교체
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