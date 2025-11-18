package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.filter.CustomRememberMeAuthenticationFilter;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.LogoutSuccessHandlerImpl;
import com.sprint.mission.discodeit.security.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.repository.InMemoryTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final LogoutSuccessHandlerImpl logoutSuccessHandler;
  private final SpaCsrfTokenRequestHandler spaCsrfTokenRequestHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      PersistentTokenBasedRememberMeServices rememberMeServices,
      CustomRememberMeAuthenticationFilter customRememberMeAuthenticationFilter) throws Exception {
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                          .csrfTokenRequestHandler(spaCsrfTokenRequestHandler))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico",
                            "/api/auth/csrf-token", "/api/auth/login", "/api/auth/logout", "/api/users",
                            "/api/auth/me", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
        .formLogin(form -> form.loginProcessingUrl("/api/auth/login")
                               .successHandler(loginSuccessHandler)
                               .failureHandler(loginFailureHandler))
        .logout(logout -> logout.logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(logoutSuccessHandler)
                                .invalidateHttpSession(true))
        .sessionManagement(session -> session.maximumSessions(1)
                                             .maxSessionsPreventsLogin(true)
                                             .sessionRegistry(sessionRegistry()))
        .rememberMe(rm -> rm.rememberMeServices(rememberMeServices))
        .userDetailsService(discodeitUserDetailsService)
        .exceptionHandling(ex -> ex.accessDeniedHandler(
            (request, response, accessDeniedException) -> response.setStatus(
                HttpServletResponse.SC_FORBIDDEN)));

    http.addFilterBefore(customRememberMeAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
//    return NoOpPasswordEncoder.getInstance(); // 암호화 전
    return new BCryptPasswordEncoder();         // 암호화 이후
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
                            .role(UserRole.ADMIN.name())
                            .implies(UserRole.CHANNEL_MANAGER.name())
                            .role(UserRole.CHANNEL_MANAGER.name())
                            .implies(UserRole.USER.name())
                            .build();
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public PersistentTokenRepository persistentTokenRepository() {
    return new InMemoryTokenRepository();
  }

  @Bean
  public PersistentTokenBasedRememberMeServices rememberMeServices(
      PersistentTokenRepository tokenRepository) {
    return new PersistentTokenBasedRememberMeServices("remember-me-key",
        discodeitUserDetailsService, tokenRepository);
  }

  @Bean
  public CustomRememberMeAuthenticationFilter customRememberMeAuthenticationFilter(
      PersistentTokenBasedRememberMeServices rememberMeServices) {
    return new CustomRememberMeAuthenticationFilter(rememberMeServices);
  }
}