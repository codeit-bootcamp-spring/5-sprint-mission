package com.sprint.mission.discodeit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		Cookie cookie = jwtTokenProvider.genereateRefreshTokenExpirationCookie();
		response.addCookie(cookie);
	}
}
