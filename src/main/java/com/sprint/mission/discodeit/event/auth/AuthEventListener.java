package com.sprint.mission.discodeit.event.auth;

import com.sprint.mission.discodeit.service.AuthMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventListener {

    private final AuthMetricsService authMetricsService;

    @Async
    @EventListener
    public void handleTokenRefreshed(TokenRefreshedEvent event) {
        authMetricsService.recordTokenRefreshAttempt(true);
        log.info("토큰 재발급 완료 (Rotation 적용): username={}", event.user().username());
    }

    @Async
    @EventListener
    public void handleTokenRefreshFailed(TokenRefreshFailedEvent event) {
        authMetricsService.recordTokenRefreshAttempt(false);
        log.warn("토큰 재발급 실패: username={}, reason={}", event.username(), event.reason());
    }
}
