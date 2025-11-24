package com.sprint.mission.discodeit.security.jwt;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider tokenProvider;
	private final ObjectMapper objectMapper;
	private final JwtRegistry<UUID> jwtRegistry;

	// 필터에서 제외할 request를 탐지할 메서드
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		return path.equals("/api/auth/refresh") || path.equals("/api/auth/login");
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			String token = resolveToken(request);
			if (StringUtils.hasText(token)) {
				if (tokenProvider.validateAccessToken(token) &&
					jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
					String username = tokenProvider.getUsernameFromToken(token);

					UserDto userDto = tokenProvider.parseAccessToken(token).userDto();
					DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto.id(), userDto, null);
					UsernamePasswordAuthenticationToken authentication
						= new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					log.debug("Set authentication for user: {}", username);
				} else {
					// 토큰 자체가 유효하지 않거나, 세션으로 부터 허가되지 않은 사용자 일 때
					log.debug("Invalid JWT token");
					// sendErrorResponse(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			}
		} catch (Exception e) {
			log.error("Authentication failed. {}", e.getMessage());
			SecurityContextHolder.clearContext();
			sendErrorResponse(e, response, "Authentication failed.", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void sendErrorResponse(
		Exception e,
		HttpServletResponse response,
		String message,
		int status
	) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(e, status);
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
