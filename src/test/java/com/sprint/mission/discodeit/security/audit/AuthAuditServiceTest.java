package com.sprint.mission.discodeit.security.audit;

import com.sprint.mission.discodeit.entity.AuthAuditLog;
import com.sprint.mission.discodeit.repository.AuthAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthAuditService 단위 테스트")
class AuthAuditServiceTest {

    @Mock
    private AuthAuditLogRepository authAuditLogRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthAuditService authAuditService;

    @Test
    @DisplayName("logLoginSuccess - 로그인 성공 감사 로그를 저장한다")
    void logLoginSuccess_SavesAuditLog() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(ipAddress);
        given(request.getHeader("User-Agent")).willReturn(userAgent);

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logLoginSuccess(userId, username, request);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        AuthAuditLog savedLog = captor.getValue();
        assertThat(savedLog.getUserId()).isEqualTo(userId);
        assertThat(savedLog.getUsername()).isEqualTo(username);
        assertThat(savedLog.getIpAddress()).isEqualTo(ipAddress);
        assertThat(savedLog.getUserAgent()).isEqualTo(userAgent);
    }

    @Test
    @DisplayName("logLoginFailure - 로그인 실패 감사 로그를 저장한다")
    void logLoginFailure_SavesAuditLog() {
        // given
        String username = "testuser";
        String reason = "Bad credentials";
        String ipAddress = "192.168.1.1";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(ipAddress);
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logLoginFailure(username, request, reason);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        AuthAuditLog savedLog = captor.getValue();
        assertThat(savedLog.getUsername()).isEqualTo(username);
        assertThat(savedLog.getDetails()).isEqualTo(reason);
    }

    @Test
    @DisplayName("logLogout - 로그아웃 감사 로그를 저장한다")
    void logLogout_SavesAuditLog() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);

        // when
        authAuditService.logLogout(userId, username, request);

        // then
        then(authAuditLogRepository).should().save(any(AuthAuditLog.class));
    }

    @Test
    @DisplayName("logTokenRefresh - 토큰 갱신 감사 로그를 저장한다")
    void logTokenRefresh_SavesAuditLog() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);

        // when
        authAuditService.logTokenRefresh(userId, username, request);

        // then
        then(authAuditLogRepository).should().save(any(AuthAuditLog.class));
    }

    @Test
    @DisplayName("logTokenRefreshFailure - 토큰 갱신 실패 감사 로그를 저장한다")
    void logTokenRefreshFailure_SavesAuditLog() {
        // given
        String reason = "Invalid refresh token";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logTokenRefreshFailure(request, reason);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        assertThat(captor.getValue().getDetails()).isEqualTo(reason);
    }

    @Test
    @DisplayName("logRoleChange - 권한 변경 감사 로그를 저장한다")
    void logRoleChange_SavesAuditLog() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String oldRole = "USER";
        String newRole = "ADMIN";

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logRoleChange(userId, username, oldRole, newRole);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        AuthAuditLog savedLog = captor.getValue();
        assertThat(savedLog.getDetails()).contains(oldRole);
        assertThat(savedLog.getDetails()).contains(newRole);
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

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logLoginSuccess(userId, username, request);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        assertThat(captor.getValue().getIpAddress()).isEqualTo("10.0.0.1");
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

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logLoginSuccess(userId, username, request);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        assertThat(captor.getValue().getUserAgent()).hasSize(500);
    }

    @Test
    @DisplayName("save - 저장 중 예외가 발생해도 에러를 던지지 않는다")
    void save_HandlesExceptionGracefully() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn("127.0.0.1");
        given(request.getHeader("User-Agent")).willReturn(null);
        given(authAuditLogRepository.save(any())).willThrow(new RuntimeException("DB error"));

        // when & then (no exception thrown)
        authAuditService.logLoginSuccess(userId, username, request);
    }

    @Test
    @DisplayName("extractIpAddress - request가 null이면 null을 반환한다")
    void extractIpAddress_NullRequest() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

        // when
        authAuditService.logLoginSuccess(userId, username, null);

        // then
        then(authAuditLogRepository).should().save(captor.capture());
        assertThat(captor.getValue().getIpAddress()).isNull();
        assertThat(captor.getValue().getUserAgent()).isNull();
    }
}
