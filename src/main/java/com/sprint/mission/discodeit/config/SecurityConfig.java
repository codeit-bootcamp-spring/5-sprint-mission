package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.security.SpaCsrfTokenRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        // CSRF 설정
        .csrf(csrf -> csrf
            // 1) 세션이 아니라 Cookie 기반 CsrfTokenRepository 사용
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // 2) SPA 환경용 CsrfTokenRequestHandler 적용
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
