package com.sprint.mission.discodeit.security.handler.jwt;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.security.dto.JwtDto;
import com.sprint.mission.discodeit.security.dto.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtSession;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("successHandler")
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;
	private final JwtRegistry<UUID> jwtRegistry;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		log.info("Login success handler called for user: {}", authentication.getName());

		DiscodeitUserDetails userDetails = (DiscodeitUserDetails)authentication.getPrincipal();

		try {
			JwtSession jwtSession = jwtTokenProvider.generateTokens(userDetails);
			String accessToken = jwtSession.getAccessToken();
			String refreshToken = jwtSession.getRefreshToken();

			log.debug("Access token generated: {}",
				accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
			log.debug("Refresh token generated");

			Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);

			JwtInformation jwtInformation = JwtInformation.builder()
				.userDto(userDetails.getUserDto())
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

			jwtRegistry.registerJwtInformation(jwtInformation);
			log.info("JWT information registered for user: {}", userDetails.getUsername());

			response.addCookie(refreshTokenCookie);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");

			JwtDto jwtDto = new JwtDto(accessToken, userDetails.getUserDto());
			String jsonResponse = objectMapper.writeValueAsString(jwtDto);

			response.getWriter().write(jsonResponse);
			response.getWriter().flush();

			log.info("Login response sent successfully for user: {}", userDetails.getUsername());

		} catch (JOSEException e) {
			log.error("Error generating JWT tokens", e);
			throw new RuntimeException(e);
		}
	}

	private Cookie createRefreshTokenCookie(String refreshToken) {
		Cookie cookie = new Cookie(JwtTokenProvider.REQUEST_TOKEN_COOKIE_NAME, refreshToken);

		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(7 * 24 * 60 * 60);

		return cookie;
	}
}