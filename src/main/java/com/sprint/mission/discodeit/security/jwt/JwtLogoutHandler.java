package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    // 로그아웃 시 Spring Security가 호출
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Cookie refreshTokenExpirationCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenExpirationCookie);

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();
                    UUID userId = jwtTokenProvider.getUserId(refreshToken);
                    jwtRegistry.invalidateJwtInformationByUserId(userId);
                });
        log.debug("JWT LogoutHandler executed : refresh token cookie cleared");
    }
}
