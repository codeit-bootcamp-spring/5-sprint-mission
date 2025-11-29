package com.sprint.mission.discodeit.security.audit;

import io.micrometer.core.instrument.MeterRegistry;
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
    @DisplayName("recordLoginSuccess - 로그인 성공 카운터를 증가시킨다")
    void recordLoginSuccess_IncrementsCounter() {
        // when
        authMetricsService.recordLoginSuccess();
        authMetricsService.recordLoginSuccess();

        // then
        assertThat(authMetricsService.getLoginSuccessCount()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("recordLoginFailure - 로그인 실패 카운터를 증가시킨다")
    void recordLoginFailure_IncrementsCounter() {
        // when
        authMetricsService.recordLoginFailure();
        authMetricsService.recordLoginFailure();
        authMetricsService.recordLoginFailure();

        // then
        assertThat(authMetricsService.getLoginFailureCount()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("recordLogout - 로그아웃 카운터를 증가시킨다")
    void recordLogout_IncrementsCounter() {
        // when
        authMetricsService.recordLogout();

        // then
        double count = meterRegistry.counter("discodeit.auth.logout").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    @DisplayName("recordTokenRefreshSuccess - 토큰 갱신 성공 카운터를 증가시킨다")
    void recordTokenRefreshSuccess_IncrementsCounter() {
        // when
        authMetricsService.recordTokenRefreshSuccess();

        // then
        double count = meterRegistry.counter("discodeit.auth.token.refresh", "result", "success").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    @DisplayName("recordTokenRefreshFailure - 토큰 갱신 실패 카운터를 증가시킨다")
    void recordTokenRefreshFailure_IncrementsCounter() {
        // when
        authMetricsService.recordTokenRefreshFailure();

        // then
        double count = meterRegistry.counter("discodeit.auth.token.refresh", "result", "failure").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    @DisplayName("recordLoginDuration - 로그인 소요 시간을 기록한다")
    void recordLoginDuration_RecordsTime() {
        // when
        authMetricsService.recordLoginDuration(150);
        authMetricsService.recordLoginDuration(250);

        // then
        double totalTime = meterRegistry.timer("discodeit.auth.login.duration").totalTime(
            java.util.concurrent.TimeUnit.MILLISECONDS);
        assertThat(totalTime).isEqualTo(400.0);
    }

    @Test
    @DisplayName("recordLoginTime - Supplier를 실행하고 소요 시간을 기록한다")
    void recordLoginTime_ExecutesSupplierAndRecordsTime() {
        // when
        String result = authMetricsService.recordLoginTime(() -> "success");

        // then
        assertThat(result).isEqualTo("success");
        long count = meterRegistry.timer("discodeit.auth.login.duration").count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("getLoginSuccessRate - 로그인 성공률을 계산한다")
    void getLoginSuccessRate_CalculatesCorrectly() {
        // given
        authMetricsService.recordLoginSuccess();
        authMetricsService.recordLoginSuccess();
        authMetricsService.recordLoginSuccess();
        authMetricsService.recordLoginFailure();

        // when
        double rate = authMetricsService.getLoginSuccessRate();

        // then
        assertThat(rate).isEqualTo(0.75);
    }

    @Test
    @DisplayName("getLoginSuccessRate - 시도가 없으면 0을 반환한다")
    void getLoginSuccessRate_ReturnsZeroWhenNoAttempts() {
        // when
        double rate = authMetricsService.getLoginSuccessRate();

        // then
        assertThat(rate).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getTokenRefreshSuccessRate - 토큰 갱신 성공률을 계산한다")
    void getTokenRefreshSuccessRate_CalculatesCorrectly() {
        // given
        authMetricsService.recordTokenRefreshSuccess();
        authMetricsService.recordTokenRefreshSuccess();
        authMetricsService.recordTokenRefreshFailure();
        authMetricsService.recordTokenRefreshFailure();

        // when
        double rate = authMetricsService.getTokenRefreshSuccessRate();

        // then
        assertThat(rate).isEqualTo(0.5);
    }

    @Test
    @DisplayName("getTokenRefreshSuccessRate - 시도가 없으면 0을 반환한다")
    void getTokenRefreshSuccessRate_ReturnsZeroWhenNoAttempts() {
        // when
        double rate = authMetricsService.getTokenRefreshSuccessRate();

        // then
        assertThat(rate).isEqualTo(0.0);
    }
}
