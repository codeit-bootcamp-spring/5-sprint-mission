package com.sprint.mission.discodeit.security;

import static com.sprint.mission.discodeit.domain.enums.Role.*;
import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;

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
	  JwtLoginSuccessHandler jwtLoginSuccessHandler,
	  LoginFailureHandler loginFailureHandler,
	  JwtAuthenticationFilter jwtAuthenticationFilter
	) throws Exception {

		http
		  .authorizeHttpRequests(auth -> auth
			// permitAll 경로 설정
			.requestMatchers(SecurityWhitelist.WHITE_LIST.toArray(String[]::new)).permitAll()
			.requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원 가입
			.anyRequest().authenticated()
		  )
		  .formLogin(login -> login
			.loginProcessingUrl("/api/auth/login")
			.successHandler(jwtLoginSuccessHandler)
			.failureHandler(loginFailureHandler)
		  )
		  .logout(logout -> logout
			.logoutUrl("/api/auth/logout")
			.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(NO_CONTENT))
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
				response.getWriter().write(objectMapper.writeValueAsString(body));
			})

		  )
		  .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
		  .addFilterBefore(
			jwtAuthenticationFilter,
			UsernamePasswordAuthenticationFilter.class
		  )
		// .rememberMe(Customizer.withDefaults())
		;

		// 8) cors 설정 추가
		http.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.addAllowedOriginPattern("*");  // 모든 Origin 허용
			config.addAllowedHeader("*");         // 모든 Header 허용
			config.addAllowedMethod("*");         // 모든 Method 허용
			config.setAllowCredentials(true);     // 쿠키/인증정보 허용 (필요할 때만)
			return config;
		}));

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

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(
	  UserDetailsService userDetailsService,
	  PasswordEncoder passwordEncoder,
	  RoleHierarchy roleHierarchy
	) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setAuthoritiesMapper(new RoleHierarchyAuthoritiesMapper(roleHierarchy));
		return provider;
	}
}
