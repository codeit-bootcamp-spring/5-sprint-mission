package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginFailureEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshFailureEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.sprint.mission.discodeit.auth.application.AuthMetricsEventListener.METRIC_PREFIX;
import static com.sprint.mission.discodeit.auth.application.AuthMetricsEventListener.TAG_FAILURE;
import static com.sprint.mission.discodeit.auth.application.AuthMetricsEventListener.TAG_REASON;
import static com.sprint.mission.discodeit.auth.application.AuthMetricsEventListener.TAG_RESULT;
import static com.sprint.mission.discodeit.auth.application.AuthMetricsEventListener.TAG_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthMetricsEventListener 단위 테스트")
class AuthMetricsEventListenerTest {

    private SimpleMeterRegistry meterRegistry;

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
        @DisplayName("로그인 성공 시 타이머 등록")
        void recordLogin_registersTimer() {
            // given
            long duration = 150L;
            LoginEvent event = new LoginEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, duration
            );

            // when
            listener.recordLogin(event);

            // then
            Timer timer = meterRegistry.find(METRIC_PREFIX + ".login")
                .tag(TAG_RESULT, TAG_SUCCESS)
                .timer();
            assertThat(timer).isNotNull();
            assertThat(timer.count()).isEqualTo(1L);
            assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isEqualTo(duration);
        }
    }

    @Nested
    @DisplayName("recordLoginFailure")
    class RecordLoginFailure {

        @Test
        @DisplayName("로그인 실패 시 타이머 등록")
        void recordLoginFailure_registersTimer() {
            // given
            long duration = 100L;
            LoginFailureEvent event = new LoginFailureEvent(duration);

            // when
            listener.recordLoginFailure(event);

            // then
            Timer timer = meterRegistry.find(METRIC_PREFIX + ".login")
                .tag(TAG_RESULT, TAG_FAILURE)
                .timer();
            assertThat(timer).isNotNull();
            assertThat(timer.count()).isEqualTo(1L);
            assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isEqualTo(duration);
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
            Counter counter = meterRegistry.find(METRIC_PREFIX + ".logout").counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }
    }

    @Nested
    @DisplayName("recordTokenRefresh")
    class RecordTokenRefresh {

        @Test
        @DisplayName("토큰 갱신 성공 시 카운터 증가")
        void recordTokenRefresh_incrementsCounter() {
            // given
            TokenRefreshEvent event = new TokenRefreshEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT
            );

            // when
            listener.recordTokenRefresh(event);

            // then
            Counter counter = meterRegistry.find(METRIC_PREFIX + ".token.refresh")
                .tag(TAG_RESULT, TAG_SUCCESS)
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
            String reason = "INVALID_REFRESH_TOKEN";
            TokenRefreshFailureEvent event = new TokenRefreshFailureEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, reason
            );

            // when
            listener.recordTokenRefreshFailure(event);

            // then
            Counter counter = meterRegistry.find(METRIC_PREFIX + ".token.refresh")
                .tag(TAG_RESULT, TAG_FAILURE)
                .tag(TAG_REASON, reason)
                .counter();
            assertThat(counter).isNotNull();
            assertThat(counter.count()).isEqualTo(1.0);
        }
    }
}
