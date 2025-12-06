package com.sprint.mission.discodeit.domain.auth.domain.event;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthMetricsEventListener 단위 테스트")
class AuthMetricsEventListenerTest {

    private MeterRegistry meterRegistry;
    private AuthMetricsEventListener listener;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_IP = "127.0.0.1";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        listener = new AuthMetricsEventListener(meterRegistry);
    }

    @Nested
    @DisplayName("recordLogin")
    class RecordLogin {

        @Test
        @DisplayName("로그인 성공 시 카운터 증가")
        void recordLogin_incrementsCounter() {
            // given
            LoginEvent event = new LoginEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, 150L
            );

            // when
            listener.recordLogin(event);

            // then
            Counter counter = meterRegistry.find("discodeit.auth.login")
                .tag("result", "success")
                .counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("로그인 성공 시 duration 타이머 기록")
        void recordLogin_recordsTimer() {
            // given
            LoginEvent event = new LoginEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, 150L
            );

            // when
            listener.recordLogin(event);

            // then
            assertThat(meterRegistry.find("discodeit.auth.login.duration")
                .tag("result", "success")
                .timer()).isNotNull();
        }
    }

    @Nested
    @DisplayName("recordLoginFailure")
    class RecordLoginFailure {

        @Test
        @DisplayName("로그인 실패 시 카운터 증가")
        void recordLoginFailure_incrementsCounter() {
            // given
            LoginFailureEvent event = new LoginFailureEvent(
                TEST_USERNAME, TEST_IP, TEST_USER_AGENT, "INVALID_CREDENTIALS", 100L
            );

            // when
            listener.recordLoginFailure(event);

            // then
            Counter counter = meterRegistry.find("discodeit.auth.login")
                .tag("result", "failure")
                .counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("로그인 실패 시 duration 타이머 기록")
        void recordLoginFailure_recordsTimer() {
            // given
            LoginFailureEvent event = new LoginFailureEvent(
                TEST_USERNAME, TEST_IP, TEST_USER_AGENT, "INVALID_CREDENTIALS", 100L
            );

            // when
            listener.recordLoginFailure(event);

            // then
            assertThat(meterRegistry.find("discodeit.auth.login.duration")
                .tag("result", "failure")
                .timer()).isNotNull();
        }
    }

    @Nested
    @DisplayName("recordLogout")
    class RecordLogout {

        @Test
        @DisplayName("로그아웃 시 카운터 증가")
        void recordLogout_incrementsCounter() {
            // given
            LogoutEvent event = new LogoutEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT
            );

            // when
            listener.recordLogout(event);

            // then
            Counter counter = meterRegistry.find("discodeit.auth.logout").counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }
    }

    @Nested
    @DisplayName("recordTokenRefreshSuccess")
    class RecordTokenRefreshSuccess {

        @Test
        @DisplayName("토큰 갱신 성공 시 카운터 증가")
        void recordTokenRefreshSuccess_incrementsCounter() {
            // given
            TokenRefreshSuccessEvent event = new TokenRefreshSuccessEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT
            );

            // when
            listener.recordTokenRefreshSuccess(event);

            // then
            Counter counter = meterRegistry.find("discodeit.auth.token.refresh")
                .tag("result", "success")
                .counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }
    }

    @Nested
    @DisplayName("recordTokenRefreshFailure")
    class RecordTokenRefreshFailure {

        @Test
        @DisplayName("토큰 갱신 실패 시 카운터 증가")
        void recordTokenRefreshFailure_incrementsCounter() {
            // given
            TokenRefreshFailureEvent event = new TokenRefreshFailureEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, "INVALID_REFRESH_TOKEN"
            );

            // when
            listener.recordTokenRefreshFailure(event);

            // then
            Counter counter = meterRegistry.find("discodeit.auth.token.refresh")
                .tag("result", "failure")
                .counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }
    }
}
