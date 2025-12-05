package com.sprint.mission.discodeit.global.aop.aspect;

import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshSuccessEvent;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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
        "@annotation(com.sprint.mission.discodeit.common.aop.annotation.AuditRefresh)";

    private final ApplicationEventPublisher eventPublisher;

    @AfterReturning(pointcut = AUDIT_REFRESH_POINTCUT, returning = "result")
    public void publishSuccessEvent(JoinPoint joinPoint, Object result) {
        if (result instanceof JwtDto jwtDto) {
            HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

            eventPublisher.publishEvent(new TokenRefreshSuccessEvent(
                jwtDto.userDto().id(),
                jwtDto.userDto().username(),
                extractIpAddress(request),
                extractUserAgent(request)
            ));
        }
    }
}
