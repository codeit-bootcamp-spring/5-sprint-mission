package com.sprint.mission.discodeit.security.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.sprint.mission.discodeit.security.filter.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final LogoutHandler logoutHandler;

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http,
		DaoAuthenticationProvider daoAuthenticationProvider,
		JwtTokenProvider jwtTokenProvider,
		UserDetailsService userDetailsService,
		LoginFailureHandler loginFailureHandler,
		ObjectMapper objectMapper,
		JwtRegistry<UUID> jwtRegistry
	) throws Exception {
		http
			.authenticationProvider(daoAuthenticationProvider)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(SecurityMatchers.PUBLIC_MATCHERS).permitAll()
				.anyRequest().hasRole(Role.USER.name())
			)
			.csrf(csrf ->
				csrf
					.ignoringRequestMatchers(SecurityMatchers.LOGOUT)
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
					.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
					.sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
			)
			.logout(logout ->
				logout
					.logoutRequestMatcher(SecurityMatchers.LOGOUT)
					.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
					.addLogoutHandler(logoutHandler)
			)
			.with(
				new JsonUsernamePasswordAuthenticationFilter.Configurer(objectMapper),
				configurer ->
					configurer
						.successHandler(new JwtLoginSuccessHandler(jwtTokenProvider, objectMapper, jwtRegistry))
						.failureHandler(loginFailureHandler)
			)
			.sessionManagement(session ->
				session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
				UsernamePasswordAuthenticationFilter.class)
		;

		return http.build();
	}
}
