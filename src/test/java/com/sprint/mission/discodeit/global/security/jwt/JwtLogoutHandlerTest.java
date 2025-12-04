package com.sprint.mission.discodeit.global.security.jwt;

import com.sprint.mission.discodeit.domain.service.AuthMetricsService;
import com.sprint.mission.discodeit.infra.event.audit.AuthAuditPublisher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtLogoutHandler 단위 테스트")
class JwtLogoutHandlerTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private AuthAuditPublisher authAuditPublisher;

    @Mock
    private AuthMetricsService authMetricsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private JwtLogoutHandler handler;

    @BeforeEach
    void setUp() {
        handler = new JwtLogoutHandler(tokenProvider, jwtRegistry, authAuditPublisher, authMetricsService);
    }

    @Test
    @DisplayName("logout - Refresh 토큰 쿠키가 있으면 JWT 정보를 무효화한다")
    void logout_WithRefreshTokenCookie_InvalidatesJwtInfo() {
        // given
        String refreshToken = "refresh-token";
        UUID userId = UUID.randomUUID();
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        Cookie expirationCookie = new Cookie("REFRESH_TOKEN", "");

        given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});
        given(tokenProvider.getUserId(refreshToken)).willReturn(userId);
        given(tokenProvider.getUsernameFromToken(refreshToken)).willReturn("testuser");
        given(tokenProvider.generateRefreshTokenExpirationCookie()).willReturn(expirationCookie);

        // when
        handler.logout(request, response, authentication);

        // then
        then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
    }

    @Test
    @DisplayName("logout - 감사 로그를 기록한다")
    void logout_LogsAudit() {
        // given
        String refreshToken = "refresh-token";
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);

        given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});
        given(tokenProvider.getUserId(refreshToken)).willReturn(userId);
        given(tokenProvider.getUsernameFromToken(refreshToken)).willReturn(username);
        given(tokenProvider.generateRefreshTokenExpirationCookie())
            .willReturn(new Cookie("REFRESH_TOKEN", ""));

        // when
        handler.logout(request, response, authentication);

        // then
        then(authAuditPublisher).should().logLogout(userId, username, request);
    }

    @Test
    @DisplayName("logout - 메트릭을 기록한다")
    void logout_RecordsMetrics() {
        // given
        String refreshToken = "refresh-token";
        UUID userId = UUID.randomUUID();
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);

        given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});
        given(tokenProvider.getUserId(refreshToken)).willReturn(userId);
        given(tokenProvider.getUsernameFromToken(refreshToken)).willReturn("testuser");
        given(tokenProvider.generateRefreshTokenExpirationCookie())
            .willReturn(new Cookie("REFRESH_TOKEN", ""));

        // when
        handler.logout(request, response, authentication);

        // then
        then(authMetricsService).should().recordLogout();
    }

    @Test
    @DisplayName("logout - 만료 쿠키를 응답에 추가한다")
    void logout_AddsExpirationCookie() {
        // given
        Cookie expirationCookie = new Cookie("REFRESH_TOKEN", "");
        expirationCookie.setMaxAge(0);

        given(request.getCookies()).willReturn(null);
        given(tokenProvider.generateRefreshTokenExpirationCookie()).willReturn(expirationCookie);

        // when
        handler.logout(request, response, authentication);

        // then
        then(response).should().addCookie(expirationCookie);
    }

    @Test
    @DisplayName("logout - Refresh 토큰 쿠키가 없으면 JWT 무효화를 시도하지 않는다")
    void logout_NoRefreshTokenCookie_DoesNotInvalidate() {
        // given
        Cookie expirationCookie = new Cookie("REFRESH_TOKEN", "");
        given(request.getCookies()).willReturn(null);
        given(tokenProvider.generateRefreshTokenExpirationCookie()).willReturn(expirationCookie);

        // when
        handler.logout(request, response, authentication);

        // then
        then(jwtRegistry).should(never()).invalidateJwtInformationByUserId(any());
    }

    @Test
    @DisplayName("logout - 다른 쿠키만 있으면 JWT 무효화를 시도하지 않는다")
    void logout_OtherCookiesOnly_DoesNotInvalidate() {
        // given
        Cookie otherCookie = new Cookie("OTHER_COOKIE", "value");
        Cookie expirationCookie = new Cookie("REFRESH_TOKEN", "");

        given(request.getCookies()).willReturn(new Cookie[]{otherCookie});
        given(tokenProvider.generateRefreshTokenExpirationCookie()).willReturn(expirationCookie);

        // when
        handler.logout(request, response, authentication);

        // then
        then(jwtRegistry).should(never()).invalidateJwtInformationByUserId(any());
    }

    @Test
    @DisplayName("logout - 토큰 파싱 실패 시에도 쿠키는 삭제한다")
    void logout_TokenParsingFails_StillDeletesCookie() {
        // given
        String refreshToken = "invalid-token";
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        Cookie expirationCookie = new Cookie("REFRESH_TOKEN", "");

        given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});
        given(tokenProvider.getUserId(refreshToken)).willThrow(new IllegalArgumentException("Invalid token"));
        given(tokenProvider.generateRefreshTokenExpirationCookie()).willReturn(expirationCookie);

        // when
        handler.logout(request, response, authentication);

        // then
        then(response).should().addCookie(expirationCookie);
        then(jwtRegistry).should(never()).invalidateJwtInformationByUserId(any());
    }
}
