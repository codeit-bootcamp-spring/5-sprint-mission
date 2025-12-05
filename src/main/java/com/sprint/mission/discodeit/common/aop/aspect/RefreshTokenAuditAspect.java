package com.sprint.mission.discodeit.common.aop.aspect;

import com.sprint.mission.discodeit.domain.auth.dto.data.JwtDto;
import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractUserAgent;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenAuditAspect {

    private static final String AUDIT_REFRESH_POINTCUT =
        "@annotation(com.sprint.mission.discodeit.common.aop.annotation.AuditRefresh)";

    private final ApplicationEventPublisher eventPublisher;

    @AfterReturning(pointcut = AUDIT_REFRESH_POINTCUT, returning = "result")
    public void publishSuccessEvent(JoinPoint joinPoint, Object result) {
        if (result instanceof JwtDto jwtDto) {
            HttpServletRequest request = getCurrentRequest();

            eventPublisher.publishEvent(new TokenRefreshSuccessEvent(
                jwtDto.userDto().id(),
                jwtDto.userDto().username(),
                extractIpAddress(request),
                extractUserAgent(request)
            ));
        }
    }

    @AfterThrowing(pointcut = AUDIT_REFRESH_POINTCUT, throwing = "exception")
    public void publishFailureEvent(JoinPoint joinPoint, InvalidTokenException exception) {
        HttpServletRequest request = getCurrentRequest();
        String username = exception.getDetails()
            .getOrDefault("username", "unknown")
            .toString();

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
        } else {
            throw new IllegalStateException("현재 요청을 가져올 수 없습니다.");
        }
    }
}
