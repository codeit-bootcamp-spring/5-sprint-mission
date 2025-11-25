package com.sprint.mission.discodeit.security;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.auth.NotAuthenticationException;
import com.sprint.mission.discodeit.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final UserDetailsService userDetailsService;

	// 필터에서 제외할 request를 탐지할 메서드
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

		String path = request.getServletPath();
		return SecurityWhitelist.matchesJwtWhiteList(path);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	  FilterChain filterChain) throws ServletException, IOException {

		String accToken = getAccessToken(request);

		if (!jwtTokenProvider.validateAccessToken(accToken)) {
			sendErrorResponse(response);
			return;
		}

		String username = jwtTokenProvider.getUsernameFromToken(accToken);
		DiscodeitUserDetails userDetails = (DiscodeitUserDetails)userDetailsService.loadUserByUsername(username);

		UsernamePasswordAuthenticationToken authentication =
		  new UsernamePasswordAuthenticationToken(
			userDetails,
			null,
			userDetails.getAuthorities()
		  );
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}

	private String getAccessToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");

		if (authorization == null || authorization.isBlank()) {
			return null;
		}

		if (!authorization.startsWith("Bearer ")) {
			return null;
		}

		String token = authorization.substring("Bearer ".length()).trim();
		return token.isEmpty() ? null : token;
	}

	private void sendErrorResponse(HttpServletResponse response) throws IOException {
		response.setStatus(SC_UNAUTHORIZED);
		response.setContentType(APPLICATION_PROBLEM_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		ErrorResponse errorResponse = ErrorResponse.of(new NotAuthenticationException(), SC_UNAUTHORIZED);
		response.getWriter()
		  .write(objectMapper.writeValueAsString(errorResponse));
	}
}
