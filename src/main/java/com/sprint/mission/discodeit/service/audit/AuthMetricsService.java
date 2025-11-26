package com.sprint.mission.discodeit.service.audit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
public class AuthMetricsService {

    private static final String METRIC_PREFIX = "discodeit.auth";

    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter logoutCounter;
    private final Counter tokenRefreshSuccessCounter;
    private final Counter tokenRefreshFailureCounter;
    private final Timer loginTimer;

    public AuthMetricsService(MeterRegistry meterRegistry) {
        this.loginSuccessCounter = Counter.builder(METRIC_PREFIX + ".login")
            .tag("result", "success")
            .description("Number of successful login attempts")
            .register(meterRegistry);

        this.loginFailureCounter = Counter.builder(METRIC_PREFIX + ".login")
            .tag("result", "failure")
            .description("Number of failed login attempts")
            .register(meterRegistry);

        this.logoutCounter = Counter.builder(METRIC_PREFIX + ".logout")
            .description("Number of logout events")
            .register(meterRegistry);

        this.tokenRefreshSuccessCounter = Counter.builder(METRIC_PREFIX + ".token.refresh")
            .tag("result", "success")
            .description("Number of successful token refresh attempts")
            .register(meterRegistry);

        this.tokenRefreshFailureCounter = Counter.builder(METRIC_PREFIX + ".token.refresh")
            .tag("result", "failure")
            .description("Number of failed token refresh attempts")
            .register(meterRegistry);

        this.loginTimer = Timer.builder(METRIC_PREFIX + ".login.duration")
            .description("Login processing time")
            .register(meterRegistry);

        log.info("AuthMetricsService initialized with metrics prefix: {}", METRIC_PREFIX);
    }

    public void recordLoginSuccess() {
        loginSuccessCounter.increment();
        log.debug("Recorded login success metric");
    }

    public void recordLoginFailure() {
        loginFailureCounter.increment();
        log.debug("Recorded login failure metric");
    }

    public void recordLogout() {
        logoutCounter.increment();
        log.debug("Recorded logout metric");
    }

    public void recordTokenRefreshSuccess() {
        tokenRefreshSuccessCounter.increment();
        log.debug("Recorded token refresh success metric");
    }

    public void recordTokenRefreshFailure() {
        tokenRefreshFailureCounter.increment();
        log.debug("Recorded token refresh failure metric");
    }

    public void recordLoginDuration(long durationMs) {
        loginTimer.record(durationMs, TimeUnit.MILLISECONDS);
        log.debug("Recorded login duration: {}ms", durationMs);
    }

    public <T> T recordLoginTime(Supplier<T> loginOperation) {
        return loginTimer.record(loginOperation);
    }

    public double getLoginSuccessCount() {
        return loginSuccessCounter.count();
    }

    public double getLoginFailureCount() {
        return loginFailureCounter.count();
    }

    public double getLoginSuccessRate() {
        double total = loginSuccessCounter.count() + loginFailureCounter.count();
        if (total == 0) {
            return 0.0;
        }
        return loginSuccessCounter.count() / total;
    }

    public double getTokenRefreshSuccessRate() {
        double total = tokenRefreshSuccessCounter.count() + tokenRefreshFailureCounter.count();
        if (total == 0) {
            return 0.0;
        }
        return tokenRefreshSuccessCounter.count() / total;
    }
}
