package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.LoginFailureEvent;
import com.sprint.mission.discodeit.auth.domain.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshFailureEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthMetricsEventListener {

    private static final String METRIC_PREFIX = "discodeit.auth";

    private final MeterRegistry meterRegistry;

    @Async
    @EventListener
    public void recordLogin(LoginEvent event) {
        log.info("Recording login metric: userId={}, username={}, duration={}ms",
            event.userId(), event.username(), event.duration());

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
        log.info("Recording login failure metric: duration={}ms", event.duration());

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
        log.info("Recording logout metric: userId={}, username={}", event.userId(), event.username());

        meterRegistry.counter(METRIC_PREFIX + ".logout").increment();
    }

    @Async
    @EventListener
    public void recordTokenRefresh(TokenRefreshEvent event) {
        log.info("Recording token refresh metric: userId={}, username={}", event.userId(), event.username());

        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", "success").increment();
    }

    @Async
    @EventListener
    public void recordTokenRefreshFailure(TokenRefreshFailureEvent event) {
        log.info("Recording token refresh failure metric: userId={}, username={}, reason={}",
            event.userId(), event.username(), event.reason());

        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", "failure").increment();
    }
}
