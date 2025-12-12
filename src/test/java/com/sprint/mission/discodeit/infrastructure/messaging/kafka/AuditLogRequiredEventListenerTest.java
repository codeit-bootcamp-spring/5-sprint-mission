package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.sprint.mission.discodeit.auth.domain.AuthAuditEventType;
import com.sprint.mission.discodeit.auth.domain.AuthAuditLog;
import com.sprint.mission.discodeit.auth.domain.AuthAuditLogRepository;
import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogRequiredEventListenerTest 단위 테스트")
class AuditLogRequiredEventListenerTest {

    @Mock
    private AuthAuditLogRepository authAuditLogRepository;

    @InjectMocks
    private AuditLogRequiredEventListener subscriber;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_IP = "127.0.0.1";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";

    @Nested
    @DisplayName("logLogin")
    class LogLogin {

        @Test
        @DisplayName("LoginEvent 수신 시 LOGIN_SUCCESS 타입으로 AuditLog 저장")
        void logLogin_savesAuditLogWithLoginSuccessType() {
            // given
            LoginEvent event = new LoginEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, 150L
            );

            ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

            // when
            subscriber.logLogin(event);

            // then
            then(authAuditLogRepository).should().save(captor.capture());

            AuthAuditLog savedLog = captor.getValue();
            assertThat(savedLog.getEventType()).isEqualTo(AuthAuditEventType.LOGIN_SUCCESS);
            assertThat(savedLog.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(savedLog.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(savedLog.getIpAddress()).isEqualTo(TEST_IP);
            assertThat(savedLog.getUserAgent()).isEqualTo(TEST_USER_AGENT);
            assertThat(savedLog.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("logLogout")
    class LogLogout {

        @Test
        @DisplayName("LogoutEvent 수신 시 LOGOUT 타입으로 AuditLog 저장")
        void logLogout_savesAuditLogWithLogoutType() {
            // given
            LogoutEvent event = new LogoutEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT
            );

            ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

            // when
            subscriber.logLogout(event);

            // then
            then(authAuditLogRepository).should().save(captor.capture());

            AuthAuditLog savedLog = captor.getValue();
            assertThat(savedLog.getEventType()).isEqualTo(AuthAuditEventType.LOGOUT);
            assertThat(savedLog.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(savedLog.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(savedLog.getIpAddress()).isEqualTo(TEST_IP);
            assertThat(savedLog.getUserAgent()).isEqualTo(TEST_USER_AGENT);
            assertThat(savedLog.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("logTokenRefresh")
    class LogTokenRefresh {

        @Test
        @DisplayName("TokenRefreshEvent 수신 시 TOKEN_REFRESH 타입으로 AuditLog 저장")
        void logTokenRefresh_savesAuditLogWithTokenRefreshType() {
            // given
            TokenRefreshEvent event = new TokenRefreshEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT
            );

            ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

            // when
            subscriber.logTokenRefresh(event);

            // then
            then(authAuditLogRepository).should().save(captor.capture());

            AuthAuditLog savedLog = captor.getValue();
            assertThat(savedLog.getEventType()).isEqualTo(AuthAuditEventType.TOKEN_REFRESH);
            assertThat(savedLog.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(savedLog.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(savedLog.getIpAddress()).isEqualTo(TEST_IP);
            assertThat(savedLog.getUserAgent()).isEqualTo(TEST_USER_AGENT);
            assertThat(savedLog.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("logRoleUpdated")
    class LogRoleUpdated {

        @Test
        @DisplayName("RoleUpdatedEvent 수신 시 ROLE_UPDATED 타입으로 AuditLog 저장")
        void logRoleUpdated_savesAuditLogWithRoleUpdatedType() {
            // given
            RoleUpdatedEvent event = new RoleUpdatedEvent(
                TEST_USER_ID, TEST_USERNAME, Role.USER, Role.CHANNEL_MANAGER
            );

            ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

            // when
            subscriber.logRoleUpdated(event);

            // then
            then(authAuditLogRepository).should().save(captor.capture());

            AuthAuditLog savedLog = captor.getValue();
            assertThat(savedLog.getEventType()).isEqualTo(AuthAuditEventType.ROLE_UPDATED);
            assertThat(savedLog.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(savedLog.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(savedLog.getIpAddress()).isNull();
            assertThat(savedLog.getUserAgent()).isNull();
            assertThat(savedLog.getDetails()).isEqualTo("Role changed from USER to CHANNEL_MANAGER");
        }

        @Test
        @DisplayName("ADMIN에서 USER로 변경 시 details에 올바른 내용 저장")
        void logRoleUpdated_savesCorrectDetailsForAdminToUser() {
            // given
            RoleUpdatedEvent event = new RoleUpdatedEvent(
                TEST_USER_ID, TEST_USERNAME, Role.ADMIN, Role.USER
            );

            ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

            // when
            subscriber.logRoleUpdated(event);

            // then
            then(authAuditLogRepository).should().save(captor.capture());

            AuthAuditLog savedLog = captor.getValue();
            assertThat(savedLog.getDetails()).isEqualTo("Role changed from ADMIN to USER");
        }
    }

    @Nested
    @DisplayName("logCredentialUpdated")
    class LogCredentialUpdatedEvent {

        @Test
        @DisplayName("CredentialUpdated 수신 시 CREDENTIAL_UPDATED 타입으로 AuditLog 저장")
        void logCredentialUpdated_savesAuditLogWithCredentialUpdatedType() {
            // given
            CredentialUpdatedEvent event = new CredentialUpdatedEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT
            );

            ArgumentCaptor<AuthAuditLog> captor = ArgumentCaptor.forClass(AuthAuditLog.class);

            // when
            subscriber.logCredentialUpdated(event);

            // then
            then(authAuditLogRepository).should().save(captor.capture());

            AuthAuditLog savedLog = captor.getValue();
            assertThat(savedLog.getEventType()).isEqualTo(AuthAuditEventType.CREDENTIAL_UPDATED);
            assertThat(savedLog.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(savedLog.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(savedLog.getIpAddress()).isEqualTo(TEST_IP);
            assertThat(savedLog.getUserAgent()).isEqualTo(TEST_USER_AGENT);
            assertThat(savedLog.getDetails()).isNull();
        }
    }
}
