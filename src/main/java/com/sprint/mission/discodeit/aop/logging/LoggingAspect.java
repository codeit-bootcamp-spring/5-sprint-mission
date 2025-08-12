package com.sprint.mission.discodeit.aop.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.sprint.mission.discodeit.controller.*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logWithTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();

        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String className = sig.getDeclaringType().getSimpleName();
        String methodName = sig.getName();

        HttpServletRequest req = currentRequest();
        String http = (req == null) ? "-" :  req.getMethod();
        String uri  = (req == null) ? "-" : req.getRequestURI();

        try {
            log.info("[Controller] {}.{} | {} {}"
                    ,className, methodName, http, uri);
            return pjp.proceed();
        } finally {
            double ms = (System.nanoTime() - start) / 1_000_000.0;
            log.info("[Controller] {}.{} | {} {} | {} ms"
                    ,className, methodName, http, uri, String.format("%.2f", ms));
        }
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}