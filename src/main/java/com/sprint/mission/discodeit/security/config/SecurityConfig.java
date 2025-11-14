package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.LogoutSuccessHandlerImpl;
import com.sprint.mission.discodeit.security.handler.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final DiscodeitUserDetailsService disUserDetailsService;
  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                          .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/api/auth/csrf-token", "/api/auth/login",
                            "/api/auth/logout", "/api/users", "/swagger-ui/**", "/v3/api-docs/**",
                            "/actuator/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
        .formLogin(form -> form.loginProcessingUrl("/api/auth/login")
                               .usernameParameter("username")
                               .passwordParameter("password")
                               .successHandler(loginSuccessHandler)
                               .failureHandler(loginFailureHandler)
                               .permitAll())
        .logout(logout -> logout.logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(new LogoutSuccessHandlerImpl())
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID"))
        .sessionManagement(session -> session
            .maximumSessions(1)
            .maxSessionsPreventsLogin(true)
            .sessionRegistry(sessionRegistry())
        )
        .rememberMe(rm -> rm
            .userDetailsService(disUserDetailsService)
            .key("uniqueAndSecret")
            .tokenValiditySeconds(7 * 24 * 60 * 60)
        )
        .userDetailsService(disUserDetailsService)
        .exceptionHandling(exception -> exception
            .accessDeniedHandler(
                (request, response, accessDeniedException) -> {
                  response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
//    return NoOpPasswordEncoder.getInstance(); // 암호화 전
    return new BCryptPasswordEncoder();         // 암호화 이후
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    String hierarchy = """
        ROLE_ADMIN > ROLE_CHANNEL_MANAGER
        ROLE_CHANNEL_MANAGER > ROLE_USER
        """;

    return RoleHierarchyImpl.fromHierarchy(hierarchy);
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}