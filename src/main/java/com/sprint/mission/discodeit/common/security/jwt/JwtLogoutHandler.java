package com.sprint.mission.discodeit.common.security.jwt;

import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtCookieProvider cookieProvider;
    private final JwtAuthEventPublisher eventPublisher;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, cookieProvider.getRefreshTokenCookieName());

        if (refreshTokenCookie != null) {
            invalidateAndPublishEvent(refreshTokenCookie.getValue(), request);
        }

        response.addCookie(cookieProvider.createExpiredRefreshTokenCookie());
        log.debug("리프레시 토큰 쿠키 삭제 완료");
    }

    private void invalidateAndPublishEvent(String refreshToken, HttpServletRequest request) {
        try {
            UUID userId = tokenProvider.getUserId(refreshToken);
            String username = tokenProvider.getUsernameFromToken(refreshToken);

            jwtRegistry.invalidateJwtInformationByUserId(userId);
            eventPublisher.publishLogout(userId, username, request);

            log.debug("JWT 로그아웃 완료: userId={}", userId);
        } catch (Exception e) {
            log.debug("리프레시 토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
        }
    }
}
