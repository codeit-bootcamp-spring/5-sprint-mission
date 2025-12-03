package com.sprint.mission.discodeit.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class AuthMetricsService {

    private static final String METRIC_PREFIX = "discodeit.auth";

    private final MeterRegistry meterRegistry;
    private final Timer loginTimer;

    public AuthMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.loginTimer = Timer.builder(METRIC_PREFIX + ".login.duration")
            .description("Login processing time")
            .register(meterRegistry);
    }

    public void recordLoginAttempt(boolean success) {
        meterRegistry.counter(METRIC_PREFIX + ".login", "result", success ? "success" : "failure").increment();
    }

    public void recordLogout() {
        meterRegistry.counter(METRIC_PREFIX + ".logout").increment();
    }

    public void recordTokenRefreshAttempt(boolean success) {
        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", success ? "success" : "failure").increment();
    }

    public <T> T recordLoginTime(Supplier<T> loginOperation) {
        return loginTimer.record(loginOperation);
    }
}
