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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry<UUID> jwtRegistry;

	@Override
	public void logout(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) {
		Cookie refreshTokenCookie = jwtTokenProvider.genereateRefreshTokenExpirationCookie();
		response.addCookie(refreshTokenCookie);

		try {
			jwtRegistry.invalidateJwtInformationByUserId(
				((DiscodeitUserDetails)authentication.getPrincipal()).getUserId());
		} catch (Exception ignore) {
		}
		log.debug("JWT logout handler executed - refresh token cookie cleared");
	}
}
