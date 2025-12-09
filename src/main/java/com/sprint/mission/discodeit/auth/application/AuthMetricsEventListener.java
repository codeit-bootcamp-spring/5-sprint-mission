package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginFailureEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshFailureEvent;
import io.micrometer.core.instrument.Counter;
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

    public static final String METRIC_PREFIX = "discodeit.auth";
    public static final String TAG_RESULT = "result";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_FAILURE = "failure";
    public static final String TAG_REASON = "reason";

    private final MeterRegistry meterRegistry;

    @Async
    @EventListener
    public void recordLogin(LoginEvent event) {
        log.debug("Recording login metric: userId={}, duration={}ms", event.userId(), event.duration());

        Timer.builder(METRIC_PREFIX + ".login")
            .description("Time taken for login request")
            .tag(TAG_RESULT, TAG_SUCCESS)
            .register(meterRegistry)
            .record(event.duration(), TimeUnit.MILLISECONDS);
    }

    @Async
    @EventListener
    public void recordLoginFailure(LoginFailureEvent event) {
        log.debug("Recording login failure metric: duration={}ms", event.duration());

        Timer.builder(METRIC_PREFIX + ".login")
            .description("Time taken for login request")
            .tag(TAG_RESULT, TAG_FAILURE)
            .register(meterRegistry)
            .record(event.duration(), TimeUnit.MILLISECONDS);
    }

    @Async
    @EventListener
    public void recordLogout(LogoutEvent event) {
        log.debug("Recording logout metric: userId={}", event.userId());

        Counter.builder(METRIC_PREFIX + ".logout")
            .description("Count of logout requests")
            .register(meterRegistry)
            .increment();
    }

    @Async
    @EventListener
    public void recordTokenRefresh(TokenRefreshEvent event) {
        log.debug("Recording token refresh metric: userId={}", event.userId());

        Counter.builder(METRIC_PREFIX + ".token.refresh")
            .tag(TAG_RESULT, TAG_SUCCESS)
            .register(meterRegistry)
            .increment();
    }

    @Async
    @EventListener
    public void recordTokenRefreshFailure(TokenRefreshFailureEvent event) {
        log.debug("Recording token refresh failure metric: userId={}, reason={}",
            event.userId(), event.reason());

        Counter.builder(METRIC_PREFIX + ".token.refresh")
            .tag(TAG_RESULT, TAG_FAILURE)
            .tag(TAG_REASON, event.reason())
            .register(meterRegistry)
            .increment();
    }
}
