package com.sprint.mission.discodeit.common.security.jwt;

import com.sprint.mission.discodeit.domain.event.auth.LoginFailureEvent;
import com.sprint.mission.discodeit.domain.event.auth.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.event.auth.LogoutEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractUserAgent;

@Component
@RequiredArgsConstructor
public class JwtAuthEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishLoginSuccess(UUID userId, String username, HttpServletRequest request) {
        eventPublisher.publishEvent(new LoginSuccessEvent(
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request)
        ));
    }

    public void publishLoginFailure(String username, String reason, HttpServletRequest request) {
        eventPublisher.publishEvent(new LoginFailureEvent(
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            reason
        ));
    }

    public void publishLogout(UUID userId, String username, HttpServletRequest request) {
        eventPublisher.publishEvent(new LogoutEvent(
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request)
        ));
    }
}
