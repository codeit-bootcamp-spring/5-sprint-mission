package com.sprint.mission.discodeit.auth.domain;

import com.sprint.mission.discodeit.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.CREDENTIAL_UPDATED;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.LOGIN_SUCCESS;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.LOGOUT;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.ROLE_UPDATED;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.TOKEN_REFRESH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AuthAuditLog 단위 테스트")
class AuthAuditLogTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "testuser";
    private static final String IP_ADDRESS = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 필드가 유효하면 AuthAuditLog 생성 성공")
        void of_withValidFields_createsLog() {
            // when
            AuthAuditLog log = AuthAuditLog.of(
                LOGIN_SUCCESS,
                USER_ID,
                USERNAME,
                IP_ADDRESS,
                USER_AGENT,
                "details"
            );

            // then
            assertThat(log.getEventType()).isEqualTo(LOGIN_SUCCESS);
            assertThat(log.getUserId()).isEqualTo(USER_ID);
            assertThat(log.getUsername()).isEqualTo(USERNAME);
            assertThat(log.getIpAddress()).isEqualTo(IP_ADDRESS);
            assertThat(log.getUserAgent()).isEqualTo(USER_AGENT);
            assertThat(log.getDetails()).isEqualTo("details");
        }

        @Test
        @DisplayName("eventType이 null이면 예외 발생")
        @SuppressWarnings("DataFlowIssue")
        void of_withNullEventType_throwsException() {
            assertThatThrownBy(() ->
                AuthAuditLog.of(null, USER_ID, USERNAME, IP_ADDRESS, USER_AGENT, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("username이 최대 길이 초과하면 예외 발생")
        void of_withTooLongUsername_throwsException() {
            // given
            String longUsername = "a".repeat(AuthAuditLog.USERNAME_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() ->
                AuthAuditLog.of(LOGIN_SUCCESS, USER_ID, longUsername, IP_ADDRESS, USER_AGENT, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("ipAddress가 최대 길이 초과하면 예외 발생")
        void of_withTooLongIpAddress_throwsException() {
            // given
            String longIpAddress = "a".repeat(AuthAuditLog.IP_ADDRESS_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() ->
                AuthAuditLog.of(LOGIN_SUCCESS, USER_ID, USERNAME, longIpAddress, USER_AGENT, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("userAgent가 최대 길이 초과하면 예외 발생")
        void of_withTooLongUserAgent_throwsException() {
            // given
            String longUserAgent = "a".repeat(AuthAuditLog.USER_AGENT_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() ->
                AuthAuditLog.of(LOGIN_SUCCESS, USER_ID, USERNAME, IP_ADDRESS, longUserAgent, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("details가 최대 길이 초과하면 예외 발생")
        void of_withTooLongDetails_throwsException() {
            // given
            String longDetails = "a".repeat(AuthAuditLog.DETAILS_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() ->
                AuthAuditLog.of(LOGIN_SUCCESS, USER_ID, USERNAME, IP_ADDRESS, USER_AGENT, longDetails))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("nullable 필드가 null이어도 생성 성공")
        void of_withNullableFields_createsLog() {
            // when
            AuthAuditLog log = AuthAuditLog.of(LOGIN_SUCCESS, null, null, null, null, null);

            // then
            assertThat(log.getEventType()).isEqualTo(LOGIN_SUCCESS);
            assertThat(log.getUserId()).isNull();
            assertThat(log.getUsername()).isNull();
            assertThat(log.getIpAddress()).isNull();
            assertThat(log.getUserAgent()).isNull();
            assertThat(log.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("login 팩토리 메서드")
    class LoginTest {

        @Test
        @DisplayName("LOGIN_SUCCESS 이벤트 타입으로 로그 생성")
        void login_createsLoginSuccessLog() {
            // when
            AuthAuditLog log = AuthAuditLog.login(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // then
            assertThat(log.getEventType()).isEqualTo(LOGIN_SUCCESS);
            assertThat(log.getUserId()).isEqualTo(USER_ID);
            assertThat(log.getUsername()).isEqualTo(USERNAME);
            assertThat(log.getIpAddress()).isEqualTo(IP_ADDRESS);
            assertThat(log.getUserAgent()).isEqualTo(USER_AGENT);
            assertThat(log.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("logout 팩토리 메서드")
    class LogoutTest {

        @Test
        @DisplayName("LOGOUT 이벤트 타입으로 로그 생성")
        void logout_createsLogoutLog() {
            // when
            AuthAuditLog log = AuthAuditLog.logout(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // then
            assertThat(log.getEventType()).isEqualTo(LOGOUT);
            assertThat(log.getUserId()).isEqualTo(USER_ID);
            assertThat(log.getUsername()).isEqualTo(USERNAME);
            assertThat(log.getIpAddress()).isEqualTo(IP_ADDRESS);
            assertThat(log.getUserAgent()).isEqualTo(USER_AGENT);
            assertThat(log.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("tokenRefresh 팩토리 메서드")
    class TokenRefreshTest {

        @Test
        @DisplayName("TOKEN_REFRESH 이벤트 타입으로 로그 생성")
        void tokenRefresh_createsTokenRefreshLog() {
            // when
            AuthAuditLog log = AuthAuditLog.tokenRefresh(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // then
            assertThat(log.getEventType()).isEqualTo(TOKEN_REFRESH);
            assertThat(log.getUserId()).isEqualTo(USER_ID);
            assertThat(log.getUsername()).isEqualTo(USERNAME);
            assertThat(log.getIpAddress()).isEqualTo(IP_ADDRESS);
            assertThat(log.getUserAgent()).isEqualTo(USER_AGENT);
            assertThat(log.getDetails()).isNull();
        }
    }

    @Nested
    @DisplayName("roleUpdated 팩토리 메서드")
    class RoleUpdatedTest {

        @Test
        @DisplayName("ROLE_UPDATED 이벤트 타입과 역할 변경 상세 정보로 로그 생성")
        void roleUpdated_createsRoleUpdatedLogWithDetails() {
            // when
            AuthAuditLog log = AuthAuditLog.roleUpdated(USER_ID, USERNAME, Role.USER, Role.ADMIN);

            // then
            assertThat(log.getEventType()).isEqualTo(ROLE_UPDATED);
            assertThat(log.getUserId()).isEqualTo(USER_ID);
            assertThat(log.getUsername()).isEqualTo(USERNAME);
            assertThat(log.getIpAddress()).isNull();
            assertThat(log.getUserAgent()).isNull();
            assertThat(log.getDetails()).isEqualTo("Role changed from USER to ADMIN");
        }
    }

    @Nested
    @DisplayName("credentialUpdated 팩토리 메서드")
    class CredentialUpdatedTest {

        @Test
        @DisplayName("CREDENTIAL_UPDATED 이벤트 타입으로 로그 생성")
        void credentialUpdated_createsCredentialUpdatedLog() {
            // when
            AuthAuditLog log = AuthAuditLog.credentialUpdated(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // then
            assertThat(log.getEventType()).isEqualTo(CREDENTIAL_UPDATED);
            assertThat(log.getUserId()).isEqualTo(USER_ID);
            assertThat(log.getUsername()).isEqualTo(USERNAME);
            assertThat(log.getIpAddress()).isEqualTo(IP_ADDRESS);
            assertThat(log.getUserAgent()).isEqualTo(USER_AGENT);
            assertThat(log.getDetails()).isNull();
        }
    }
}
