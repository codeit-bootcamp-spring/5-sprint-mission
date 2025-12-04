package com.sprint.mission.discodeit.security.audit;

import com.sprint.mission.discodeit.domain.entity.AuthAuditEventType;
import com.sprint.mission.discodeit.infra.event.audit.AuthAuditPublisher;
import com.sprint.mission.discodeit.infra.event.auth.AuthAuditEvent;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthAuditService 단위 테스트")
class AuthAuditPublisherTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthAuditPublisher authAuditPublisher;

    @Test
    @DisplayName("logLoginSuccess - 로그인 성공 이벤트를 발행한다")
    void logLoginSuccess_PublishesEvent() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(ipAddress);
        given(request.getHeader("User-Agent")).willReturn(userAgent);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLoginSuccess(userId, username, request);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        AuthAuditEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo(AuthAuditEventType.LOGIN_SUCCESS);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.username()).isEqualTo(username);
        assertThat(event.ipAddress()).isEqualTo(ipAddress);
        assertThat(event.userAgent()).isEqualTo(userAgent);
    }

    @Test
    @DisplayName("logLoginFailure - 로그인 실패 이벤트를 발행한다")
    void logLoginFailure_PublishesEvent() {
        // given
        String username = "testuser";
        String reason = "Bad credentials";
        String ipAddress = "192.168.1.1";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(ipAddress);
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLoginFailure(username, request, reason);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        AuthAuditEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo(AuthAuditEventType.LOGIN_FAILURE);
        assertThat(event.username()).isEqualTo(username);
        assertThat(event.details()).isEqualTo(reason);
    }

    @Test
    @DisplayName("logLogout - 로그아웃 이벤트를 발행한다")
    void logLogout_PublishesEvent() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLogout(userId, username, request);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        AuthAuditEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo(AuthAuditEventType.LOGOUT);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.username()).isEqualTo(username);
    }

    @Test
    @DisplayName("logTokenRefresh - 토큰 갱신 이벤트를 발행한다")
    void logTokenRefresh_PublishesEvent() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logTokenRefresh(userId, username, request);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        AuthAuditEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo(AuthAuditEventType.TOKEN_REFRESH);
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("logTokenRefreshFailure - 토큰 갱신 실패 이벤트를 발행한다")
    void logTokenRefreshFailure_PublishesEvent() {
        // given
        String reason = "Invalid refresh token";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logTokenRefreshFailure(request, reason);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        AuthAuditEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo(AuthAuditEventType.TOKEN_REFRESH_FAILURE);
        assertThat(event.details()).isEqualTo(reason);
    }

    @Test
    @DisplayName("logRoleChange - 권한 변경 이벤트를 발행한다")
    void logRoleChange_PublishesEvent() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String oldRole = "USER";
        String newRole = "ADMIN";

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logRoleChange(userId, username, oldRole, newRole);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        AuthAuditEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo(AuthAuditEventType.ROLE_CHANGE);
        assertThat(event.details()).contains(oldRole);
        assertThat(event.details()).contains(newRole);
    }

    @Test
    @DisplayName("extractIpAddress - X-Forwarded-For 헤더가 있으면 첫 번째 IP를 반환한다")
    void extractIpAddress_WithXForwardedFor() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String forwardedFor = "10.0.0.1, 10.0.0.2, 10.0.0.3";

        given(request.getHeader("X-Forwarded-For")).willReturn(forwardedFor);
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLoginSuccess(userId, username, request);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().ipAddress()).isEqualTo("10.0.0.1");
    }

    @Test
    @DisplayName("extractUserAgent - User-Agent가 500자를 초과하면 잘라서 저장한다")
    void extractUserAgent_TruncatesLongUserAgent() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String longUserAgent = "A".repeat(600);

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(longUserAgent);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLoginSuccess(userId, username, request);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().userAgent()).hasSize(500);
    }

    @Test
    @DisplayName("extractIpAddress - request가 null이면 null을 반환한다")
    void extractIpAddress_NullRequest() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLoginSuccess(userId, username, null);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().ipAddress()).isNull();
        assertThat(captor.getValue().userAgent()).isNull();
    }

    @Test
    @DisplayName("extractIpAddress - X-Forwarded-For가 빈 문자열이면 remoteAddr을 반환한다")
    void extractIpAddress_WithBlankXForwardedFor_ReturnsRemoteAddr() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String remoteAddr = "192.168.1.100";

        given(request.getHeader("X-Forwarded-For")).willReturn("   ");
        given(request.getRemoteAddr()).willReturn(remoteAddr);
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditEvent> captor = ArgumentCaptor.forClass(AuthAuditEvent.class);

        // when
        authAuditPublisher.logLoginSuccess(userId, username, request);

        // then
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().ipAddress()).isEqualTo(remoteAddr);
    }
}
