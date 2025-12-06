package com.sprint.mission.discodeit.domain.auth.application.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MeasureLoginDurationAspect {

    private static final String MEASURE_LOGIN_DURATION_POINTCUT =
        "@annotation(com.sprint.mission.discodeit.domain.auth.application.aspect.MeasureLoginDuration)";

    public static final String LOGIN_START_TIME_ATTRIBUTE = "loginStartTime";

    @Around(MEASURE_LOGIN_DURATION_POINTCUT)
    public Object measureLoginDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = extractRequest(joinPoint);

        if (request != null) {
            request.setAttribute(LOGIN_START_TIME_ATTRIBUTE, System.currentTimeMillis());
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest extractRequest(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest httpServletRequest) {
                return httpServletRequest;
            }
        }
        return null;
    }

    public static long calculateDuration(HttpServletRequest request) {
        Object startTimeAttr = request.getAttribute(LOGIN_START_TIME_ATTRIBUTE);
        if (startTimeAttr instanceof Long startTime) {
            return System.currentTimeMillis() - startTime;
        }
        return -1;
    }
}
