package com.sprint.mission.discodeit.infra.event.listener;

import com.sprint.mission.discodeit.infra.event.auth.LoginFailureEvent;
import com.sprint.mission.discodeit.infra.event.auth.LoginSuccessEvent;
import com.sprint.mission.discodeit.infra.event.auth.LogoutEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshSuccessEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class AuthMetricsEventListener {

    private static final String METRIC_PREFIX = "discodeit.auth";

    private final MeterRegistry meterRegistry;
    private final Timer loginTimer;

    public AuthMetricsEventListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.loginTimer = Timer.builder(METRIC_PREFIX + ".login.duration")
            .description("Login processing time")
            .register(meterRegistry);
    }

    @Async
    @EventListener
    public void recordLoginSuccess(LoginSuccessEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".login", "result", "success").increment();
    }

    @Async
    @EventListener
    public void recordLoginFailure(LoginFailureEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".login", "result", "failure").increment();
    }

    @Async
    @EventListener
    public void recordLogout(LogoutEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".logout").increment();
    }

    @Async
    @EventListener
    public void recordTokenRefreshSuccess(TokenRefreshSuccessEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", "success").increment();
    }

    @Async
    @EventListener
    public void recordTokenRefreshFailure(TokenRefreshFailureEvent event) {
        meterRegistry.counter(METRIC_PREFIX + ".token.refresh", "result", "failure").increment();
    }

    public <T> T recordLoginTime(Supplier<T> loginOperation) {
        return loginTimer.record(loginOperation);
    }
}
