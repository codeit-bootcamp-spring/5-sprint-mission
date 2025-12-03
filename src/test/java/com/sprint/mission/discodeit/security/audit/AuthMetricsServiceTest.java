package com.sprint.mission.discodeit.security.audit;

import com.sprint.mission.discodeit.service.AuthMetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthMetricsService 단위 테스트")
class AuthMetricsServiceTest {

    private MeterRegistry meterRegistry;
    private AuthMetricsService authMetricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        authMetricsService = new AuthMetricsService(meterRegistry);
    }

    @Test
    @DisplayName("recordLoginAttempt - 로그인 성공 시 성공 카운터를 증가시킨다")
    void recordLoginAttempt_Success_IncrementsSuccessCounter() {
        // when
        authMetricsService.recordLoginAttempt(true);
        authMetricsService.recordLoginAttempt(true);

        // then
        Counter successCounter = meterRegistry.find("discodeit.auth.login").tag("result", "success").counter();
        Counter failureCounter = meterRegistry.find("discodeit.auth.login").tag("result", "failure").counter();

        assertThat(successCounter).isNotNull();
        assertThat(successCounter.count()).isEqualTo(2.0);
        assertThat(failureCounter).isNull(); // Or count is 0, depending on registry impl. isNull is safer.
    }

    @Test
    @DisplayName("recordLoginAttempt - 로그인 실패 시 실패 카운터를 증가시킨다")
    void recordLoginAttempt_Failure_IncrementsFailureCounter() {
        // when
        authMetricsService.recordLoginAttempt(false);
        authMetricsService.recordLoginAttempt(false);
        authMetricsService.recordLoginAttempt(false);

        // then
        Counter successCounter = meterRegistry.find("discodeit.auth.login").tag("result", "success").counter();
        Counter failureCounter = meterRegistry.find("discodeit.auth.login").tag("result", "failure").counter();

        assertThat(failureCounter).isNotNull();
        assertThat(failureCounter.count()).isEqualTo(3.0);
        assertThat(successCounter).isNull();
    }

    @Test
    @DisplayName("recordLogout - 로그아웃 카운터를 증가시킨다")
    void recordLogout_IncrementsCounter() {
        // when
        authMetricsService.recordLogout();
        authMetricsService.recordLogout();

        // then
        Counter logoutCounter = meterRegistry.find("discodeit.auth.logout").counter();
        assertThat(logoutCounter).isNotNull();
        assertThat(logoutCounter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("recordTokenRefreshAttempt - 토큰 갱신 성공 시 성공 카운터를 증가시킨다")
    void recordTokenRefreshAttempt_Success_IncrementsSuccessCounter() {
        // when
        authMetricsService.recordTokenRefreshAttempt(true);

        // then
        Counter successCounter = meterRegistry.find("discodeit.auth.token.refresh").tag("result", "success").counter();
        Counter failureCounter = meterRegistry.find("discodeit.auth.token.refresh").tag("result", "failure").counter();

        assertThat(successCounter).isNotNull();
        assertThat(successCounter.count()).isEqualTo(1.0);
        assertThat(failureCounter).isNull();
    }

    @Test
    @DisplayName("recordTokenRefreshAttempt - 토큰 갱신 실패 시 실패 카운터를 증가시킨다")
    void recordTokenRefreshAttempt_Failure_IncrementsFailureCounter() {
        // when
        authMetricsService.recordTokenRefreshAttempt(false);
        authMetricsService.recordTokenRefreshAttempt(false);

        // then
        Counter successCounter = meterRegistry.find("discodeit.auth.token.refresh").tag("result", "success").counter();
        Counter failureCounter = meterRegistry.find("discodeit.auth.token.refresh").tag("result", "failure").counter();

        assertThat(failureCounter).isNotNull();
        assertThat(failureCounter.count()).isEqualTo(2.0);
        assertThat(successCounter).isNull();
    }

    @Test
    @DisplayName("recordLoginTime - Supplier를 실행하고 소요 시간을 기록한다")
    void recordLoginTime_ExecutesSupplierAndRecordsTime() {
        // when
        String result = authMetricsService.recordLoginTime(() -> {
            try {
                Thread.sleep(50); // Simulate work
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "success";
        });

        // then
        assertThat(result).isEqualTo("success");

        Timer loginTimer = meterRegistry.find("discodeit.auth.login.duration").timer();
        assertThat(loginTimer).isNotNull();
        assertThat(loginTimer.count()).isEqualTo(1);
        assertThat(loginTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS)).isGreaterThanOrEqualTo(50.0);
    }
}
