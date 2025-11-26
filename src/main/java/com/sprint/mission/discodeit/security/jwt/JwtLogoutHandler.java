package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@org.springframework.context.annotation.Profile("!test")
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                .filter(cookie -> cookie.getName()
                    .equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();
                    try {
                        UUID userId = tokenProvider.getUserId(refreshToken);
                        jwtRegistry.invalidateJwtInformationByUserId(userId);
                        log.debug("JWT 로그아웃 핸들러 실행 - 사용자 {} JWT 정보 삭제 완료", userId);
                    } catch (Exception e) {
                        log.debug("Failed to extract user ID from refresh token: {}",
                            e.getMessage());
                    }
                });
        }

        Cookie expiredCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
        response.addCookie(expiredCookie);

        log.debug("JWT 로그아웃 핸들러 실행 - 리프레시 토큰 쿠키 삭제 완료");
    }
}
