package com.sprint.mission.discodeit.security.config;

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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.csrf.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.handler.Http403ForbiddenAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.HttpStatusReturningLogoutSuccessHandler;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final SessionRegistry sessionRegistry;
	private final LoginFailureHandler failureHandler;
	private final LoginSuccessHandler successHandler;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain restFilterChain(HttpSecurity http) throws Exception {
		return http
			.securityMatcher("/api/**")
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/", "/index.html",
					"/assets/**",
					"/favicon.ico",
					"/error"
				)
				.permitAll()
				.requestMatchers("/api/auth/csrf-token", "/api/auth/login", "/api/auth/logout")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users")
				.permitAll()
				.requestMatchers("/api/users")
				.permitAll()
				.anyRequest()
				.authenticated()
			)
			.formLogin(login -> login
				.loginProcessingUrl("/api/auth/login")
				.successHandler(successHandler)
				.failureHandler(failureHandler)
			)
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.logoutSuccessUrl("/")
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
			)
			.sessionManagement(session -> session
				.sessionConcurrency(concurrency -> concurrency
					.maximumSessions(1)
					.sessionRegistry(sessionRegistry)
				)

			)
			.csrf(csrf -> csrf
				.ignoringRequestMatchers("/api/auth/logout")
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(new Http403ForbiddenEntryPoint())
				.accessDeniedHandler(new Http403ForbiddenAccessDeniedHandler(objectMapper))
			)
			.build();
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
	static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
		RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchy);
		return handler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
}
