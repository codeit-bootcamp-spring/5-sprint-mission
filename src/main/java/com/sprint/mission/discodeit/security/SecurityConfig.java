package com.sprint.mission.discodeit.security;

import static com.sprint.mission.discodeit.domain.enums.Role.*;
import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain filterChain(
	  HttpSecurity http,
	  LoginSuccessHandler loginSuccessHandler,
	  LoginFailureHandler loginFailureHandler
	) throws Exception {

		http
		  .authorizeHttpRequests(auth -> auth
			// permitAll 경로 설정
			.requestMatchers("/api/auth/login", "/error", "/", "/index.html").permitAll()
			.requestMatchers("/api/auth/csrf-token").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원 가입
			.requestMatchers(
			  "/css/**",
			  "/js/**",
			  "/images/**",
			  "/webjars/**",
			  "/favicon.ico",
			  "/swagger-ui/**",
			  "/assets/**",
			  "/actuator/**"
			).permitAll()
			.anyRequest().authenticated()
		  )
		  .formLogin(login -> login
			.loginProcessingUrl("/api/auth/login")
			.successHandler(loginSuccessHandler)
			.failureHandler(loginFailureHandler)
		  )
		  .logout(logout -> logout
			.logoutUrl("/api/auth/logout")
			.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
		  )
		  .csrf(csrf -> csrf
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())

		  )
		  .exceptionHandling(ex -> ex
			.accessDeniedHandler((request, response, accessDeniedException) -> {
				response.setStatus(FORBIDDEN.value());
				response.setContentType("application/json");
				ErrorResponse body = ErrorResponse.of(NOT_AUTHORIZED, FORBIDDEN.value(), accessDeniedException);

				response.getWriter().write(objectMapper.writeValueAsString(body));
			})
			.authenticationEntryPoint((request, response, authException) -> {
				response.setStatus(FORBIDDEN.value());
				response.setContentType("application/json");
				ErrorResponse body = ErrorResponse.of(NOT_AUTHORIZED, FORBIDDEN.value(), authException);
			})

		  )
		;

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
		  .role(ADMIN.name()).implies(CHANNEL_MANAGER.name())
		  .role(CHANNEL_MANAGER.name()).implies(USER.name())

		  .build();
	}

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy);
		return expressionHandler;
	}
}
