package com.sprint.mission.discodeit.security.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sprint.mission.discodeit.security.config.SecurityMatchers;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String token = resolveToken(request);

		if (!isMatched(request)) {
			try {
				if (token != null && jwtTokenProvider.validateAccessToken(token)) {
					String username = jwtTokenProvider.getUsername(token);
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(authentication);

					filterChain.doFilter(request, response);
				} else {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setCharacterEncoding("UTF-8");
				}
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}

		} else {
			filterChain.doFilter(request, response);
		}

	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);  // "Bearer " 제거
		}

		return null;
	}

	private boolean isMatched(HttpServletRequest request) {
		return Arrays.stream(SecurityMatchers.PUBLIC_MATCHERS)
			.anyMatch(requestmatcher -> requestmatcher.matches(request));
	}
}
