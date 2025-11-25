package com.sprint.mission.discodeit.configuration;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.Http403ForbiddenAccessDeniedHandler;
import com.sprint.mission.discodeit.security.HttpStatusReturningLogoutSuccessHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	// TODO: 인증/인가 설정 분리 권장: 하나의 SecurityConfig에 모든 설정이 들어가 있어 파일이 너무 비대함
	/* 권장 구조
	WebSecurityConfig (HttpSecurity, FilterChain)
	MethodSecurityConfig (이미 Handler 하나 추가된 상태라 분리 권장)
	AuthenticationProviderConfig
	RoleHierarchyConfig
	 */
	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http,
		JwtLoginSuccessHandler jwtLoginSuccessHandler,
		LoginFailureHandler loginFailureHandler,
		JwtLogoutHandler jwtLogoutHandler,
		HttpStatusReturningLogoutSuccessHandler logoutSuccessHandler,
		Http403ForbiddenAccessDeniedHandler accessDeniedHandler,
		JwtAuthenticationFilter jwtAuthenticationFilter
	) throws Exception {
		http // TODO: permitAll 설정 정리정돈
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/login", "/", "/error", "/index.html").permitAll()
				.requestMatchers("/api/auth/refresh").permitAll()
				.requestMatchers("/favicon.ico", "/static/**", "/assets/**", "/webjars/**").permitAll()
				.requestMatchers("/logout").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users").permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
				.anyRequest().authenticated()
			)
			.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
			)
			.formLogin(login -> login
				.loginProcessingUrl("/api/auth/login")
				.successHandler(jwtLoginSuccessHandler)
				.failureHandler(loginFailureHandler)
			)
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.addLogoutHandler(jwtLogoutHandler)
				.logoutSuccessHandler(logoutSuccessHandler)
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(new Http403ForbiddenEntryPoint())
				.accessDeniedHandler(accessDeniedHandler)
			)
			.sessionManagement(management -> management
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

		;
		return http.build();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
			.role(Role.ADMIN.name())
			.implies(Role.CHANNEL_MANAGER.name())
			.role(Role.CHANNEL_MANAGER.name())
			.implies(Role.USER.name())
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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
	public DaoAuthenticationProvider authenticationProvider(
		UserDetailsService userDetailsService,
		PasswordEncoder passwordEncoder,
		RoleHierarchy roleHierarchy
	) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setAuthoritiesMapper(new RoleHierarchyAuthoritiesMapper(roleHierarchy));
		return provider;
	}

	@Bean
	static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
		RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchy);
		return handler;
	}

	@Profile("dev")
	@Bean
	public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
		return args -> {
			int filterSize = filterChain.getFilters().size();
			List<String> filterNames = IntStream.range(0, filterSize)
				.mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
					filterChain.getFilters().get(idx).getClass()))
				.toList();
			log.debug("Debug Filter Chain...\n{}", String.join(System.lineSeparator(), filterNames));
		};
	}

	@Bean
	public JwtRegistry<UUID> jwtRegistry(JwtTokenProvider jwtTokenProvider) {
		return new InMemoryJwtRegistry(1, jwtTokenProvider);
	}
}
