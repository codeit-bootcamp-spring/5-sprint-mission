package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry jwtRegistry;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		DiscodeitUserDetails userDetails = (DiscodeitUserDetails)authentication.getPrincipal();
		UUID userId = userDetails.getUserId();
		jwtRegistry.invalidateJwtInformationByUserId(userId);

		Cookie cookie = jwtTokenProvider.genereateRefreshTokenExpirationCookie();
		response.addCookie(cookie);
	}
}
