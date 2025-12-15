package com.sprint.mission.discodeit.global.security.jwt;

import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.UUID;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    @Value("${discodeit.jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    private final JwtTokenProvider tokenProvider;
    private final JwtCookieProvider cookieProvider;
    private final JwtRegistry jwtRegistry;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, refreshTokenCookieName);

        if (refreshTokenCookie != null) {
            invalidateAndPublishEvent(refreshTokenCookie.getValue(), request);
        }

        response.addCookie(cookieProvider.createExpiredRefreshTokenCookie());
        log.debug("리프레시 토큰 쿠키 삭제 완료");
    }

    private void invalidateAndPublishEvent(String refreshToken, HttpServletRequest request) {
        try {
            UUID userId = tokenProvider.getUserIdFromToken(refreshToken);
            String username = tokenProvider.getUsernameFromToken(refreshToken);

            jwtRegistry.invalidateJwtInformationByUserId(userId);

            eventPublisher.publishEvent(new LogoutEvent(
                userId,
                username,
                extractIpAddress(request),
                extractUserAgent(request)
            ));

            log.debug("JWT 로그아웃 완료: userId={}", userId);
        } catch (Exception e) {
            log.debug("리프레시 토큰 파싱 실패: {}", e.getMessage());
        }
    }
}
