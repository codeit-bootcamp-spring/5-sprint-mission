package com.sprint.mission.discodeit.security.jwt;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry<UUID> jwtRegistry;

	@Override
	@CacheEvict(value = "users", allEntries = true)
	public void logout(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) {
		Cookie refreshTokenCookie = jwtTokenProvider.genereateRefreshTokenExpirationCookie();
		response.addCookie(refreshTokenCookie);

		Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
			.findFirst()
			.ifPresent(cookie -> {
				String refreshToken = cookie.getValue();
				UUID userId = jwtTokenProvider.getUserId(refreshToken);
				jwtRegistry.invalidateJwtInformationByUserId(userId);
			});

		log.debug("JWT logout handler executed - refresh token cookie cleared");
	}
}
