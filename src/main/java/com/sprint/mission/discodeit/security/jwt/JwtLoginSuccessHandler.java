package com.sprint.mission.discodeit.security.jwt;

import java.io.IOException;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.user.JwtDto;
import com.sprint.mission.discodeit.dto.user.JwtInformation;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry<UUID> jwtRegistry;

	@Override
	@CacheEvict(value = "users", allEntries = true)
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
			try {
				// 토큰 발급 코드
				String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
				String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

				// refreshToken은 쿠키에 담고, httponly 옵션 등을 활성화하여 쿠키로 전달예정
				Cookie refreshCookie = jwtTokenProvider.genereateRefreshTokenCookie(refreshToken);
				response.addCookie(refreshCookie);

				// accessToken은 브라우저에 저장할수 있도록 일반 응답(json, body)으로 보낼 예정
				JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

				// JWT 세션 = jwtRegistry에 발급된 토큰 저장
				jwtRegistry.registerJwtInformation(
					new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken));

				log.info("Successfully registered JWT for user {}", userDetails.getUserDto());
			} catch (JOSEException e) {
				log.error(e.getMessage());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ErrorResponse errorResponse = new ErrorResponse(
					e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
			}
		} else {
			log.error("UNAUTHORIZED Error!!");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
