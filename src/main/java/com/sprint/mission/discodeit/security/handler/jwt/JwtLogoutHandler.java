package com.sprint.mission.discodeit.security.handler.jwt;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component("logoutHandler")
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

	private final JwtRegistry<UUID> jwtRegistry;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		resolveRefreshToken(request)
			.ifPresent(refreshToken -> {
				invalidateRefreshTokenCookie(response);
			});
	}

	private Optional<String> resolveRefreshToken(HttpServletRequest request) {
		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(JwtTokenProvider.REQUEST_TOKEN_COOKIE_NAME))
			.findFirst()
			.map(Cookie::getValue);
	}

	private void invalidateRefreshTokenCookie(HttpServletResponse response) {
		Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REQUEST_TOKEN_COOKIE_NAME, "");
		refreshTokenCookie.setMaxAge(0);
		refreshTokenCookie.setHttpOnly(true);
		response.addCookie(refreshTokenCookie);
	}
}
