package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.service.AuthAuditService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!test")
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final AuthAuditService authAuditService;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request,
            JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);

        if (refreshTokenCookie != null) {
            invalidateUserJwt(refreshTokenCookie.getValue(), request);
        }

        response.addCookie(tokenProvider.generateRefreshTokenExpirationCookie());
        log.debug("JWT 로그아웃 핸들러 실행 - 리프레시 토큰 쿠키 삭제 완료");
    }

    private void invalidateUserJwt(String refreshToken, HttpServletRequest request) {
        try {
            UUID userId = tokenProvider.getUserId(refreshToken);
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            jwtRegistry.invalidateJwtInformationByUserId(userId);

            authAuditService.logLogout(userId, username, request);

            log.debug("JWT 로그아웃 핸들러 실행 - 사용자 {} JWT 정보 삭제 완료", userId);
        } catch (Exception e) {
            log.debug("Failed to extract user ID from refresh token: {}", e.getMessage());
        }
    }
}
