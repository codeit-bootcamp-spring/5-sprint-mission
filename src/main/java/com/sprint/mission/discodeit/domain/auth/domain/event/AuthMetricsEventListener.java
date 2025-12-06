package com.sprint.mission.discodeit.domain.auth.domain.event;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuthMetricsEventListener {

    private static final String METRIC_PREFIX = "discodeit.auth";

    private final MeterRegistry meterRegistry;

    @Async
    @EventListener
    public void recordLogin(LoginEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".login", "result", "success").increment();

        Timer.builder(METRIC_PREFIX + ".login.duration")
            .description("Time taken for login request")
            .tag("result", "success")
            .register(meterRegistry)
            .record(event.duration(), TimeUnit.MILLISECONDS);
    }

    @Async
    @EventListener
    public void recordLoginFailure(LoginFailureEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".login", "result", "failure").increment();

        Timer.builder(METRIC_PREFIX + ".login.duration")
            .description("Time taken for login request")
            .tag("result", "failure")
            .register(meterRegistry)
            .record(event.duration(), TimeUnit.MILLISECONDS);
    }

    @Async
    @EventListener
    public void recordLogout(LogoutEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".logout").increment();
    }

    @Async
    @EventListener
    public void recordTokenRefresh(TokenRefreshEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", "success").increment();
    }

    @Async
    @EventListener
    public void recordTokenRefreshFailure(TokenRefreshFailureEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", "failure").increment();
    }
}
