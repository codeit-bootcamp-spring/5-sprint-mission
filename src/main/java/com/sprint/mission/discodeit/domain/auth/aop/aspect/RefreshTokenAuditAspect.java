package com.sprint.mission.discodeit.domain.auth.aop.aspect;

import com.sprint.mission.discodeit.domain.auth.dto.response.JwtResponse;
import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshSuccessEvent;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenAuditAspect {

    private static final String AUDIT_REFRESH_POINTCUT =
        "@annotation(com.sprint.mission.discodeit.domain.auth.aop.annotation.AuditLogTokenRefresh)";

    private final ApplicationEventPublisher eventPublisher;

    @AfterReturning(pointcut = AUDIT_REFRESH_POINTCUT, returning = "result")
    public void publishSuccessEvent(Object result) {
        if (result instanceof JwtResponse jwtResponse) {
            HttpServletRequest request = getCurrentRequest();

            eventPublisher.publishEvent(new TokenRefreshSuccessEvent(
                jwtResponse.userDto().id(),
                jwtResponse.userDto().username(),
                extractIpAddress(request),
                extractUserAgent(request)
            ));
        }
    }

    @AfterThrowing(pointcut = AUDIT_REFRESH_POINTCUT, throwing = "exception")
    public void publishFailureEvent(Exception exception) {
        HttpServletRequest request = getCurrentRequest();
        String username = extractUsername(exception);

        log.warn("Token refresh failed. user: {}, reason: {}", username, exception.getMessage());

        eventPublisher.publishEvent(new TokenRefreshFailureEvent(
            null,
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            exception.getMessage()
        ));
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }

        throw new IllegalStateException("No active HttpServletRequest found for auditing.");
    }

    private String extractUsername(Exception exception) {
        if (exception instanceof DiscodeitException discodeitException) {
            Object username = discodeitException.getDetails().get("username");
            if (username != null) {
                return username.toString();
            }
        }
        return "unknown";
    }

}
