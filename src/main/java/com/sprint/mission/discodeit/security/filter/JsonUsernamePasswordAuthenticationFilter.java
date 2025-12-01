package com.sprint.mission.discodeit.security.filter;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.security.config.SecurityMatchers;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final ObjectMapper objectMapper;
	private final StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
		HttpServletResponse response) throws AuthenticationException {

		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
				"Authentication method not supported: " + request.getMethod());
		}

		try {
			String contentType = request.getContentType();
			String username = null;
			String password = null;

			if (contentType != null && contentType.contains("application/json")) {
				LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
					LoginRequest.class);
				username = loginRequest.username();
				password = loginRequest.password();
				log.info("JSON login attempt - username: {}", username);
			} else if (contentType != null && contentType.contains("multipart/form-data")) {
				if (multipartResolver.isMultipart(request)) {
					MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
					username = multipartRequest.getParameter("username");
					password = multipartRequest.getParameter("password");
					log.info("Multipart form login attempt - username: {}", username);
				} else {
					username = request.getParameter("username");
					password = request.getParameter("password");
					log.info("Form login attempt - username: {}", username);
				}
			} else {
				username = request.getParameter("username");
				password = request.getParameter("password");
				log.info("Form login attempt - username: {}", username);
			}

			if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
				throw new AuthenticationServiceException("Username or password is missing");
			}

			UsernamePasswordAuthenticationToken authRequest =
				new UsernamePasswordAuthenticationToken(username, password);

			setDetails(request, authRequest);
			return this.getAuthenticationManager().authenticate(authRequest);

		} catch (IOException e) {
			log.error("Request parsing failed", e);
			throw new AuthenticationServiceException("Request parsing failed", e);
		}
	}

	public static JsonUsernamePasswordAuthenticationFilter createDefault(
		ObjectMapper objectMapper,
		AuthenticationManager authenticationManager,
		SessionAuthenticationStrategy sessionAuthenticationStrategy,
		RememberMeServices rememberMeServices
	) {
		JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter(
			objectMapper);
		filter.setRequiresAuthenticationRequestMatcher(SecurityMatchers.LOGIN);
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper));
		filter.setAuthenticationFailureHandler(new LoginFailureHandler());
		filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		filter.setRememberMeServices(rememberMeServices);
		return filter;
	}

	public static class Configurer extends
		AbstractAuthenticationFilterConfigurer<HttpSecurity, Configurer, JsonUsernamePasswordAuthenticationFilter> {

		public Configurer(ObjectMapper objectMapper) {
			super(new JsonUsernamePasswordAuthenticationFilter(objectMapper), SecurityMatchers.LOGIN_URL);
		}

		@Override
		protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
			return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
		}

		@Override
		public void init(HttpSecurity http) throws Exception {
			loginProcessingUrl(SecurityMatchers.LOGIN_URL);
		}
	}
}