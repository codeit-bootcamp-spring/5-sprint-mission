package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_METHOD = "requestMethod";
    private static final String REQUEST_URI = "requestUri";
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        String requestId = UUID.randomUUID().toString();
        String requestMethod = request.getMethod();
        String requestUri = request.getRequestURI();

        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, requestMethod);
        MDC.put(REQUEST_URI, requestUri);
        MDC.put(REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));

        response.setHeader(REQUEST_ID_HEADER, requestId);

        log.debug("Request started: {} {}", requestMethod, requestUri);
        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        Exception exception
    ) {
        String requestId = MDC.get(REQUEST_ID);
        String requestMethod = MDC.get(REQUEST_METHOD);
        String requestUri = MDC.get(REQUEST_URI);
        int status = response.getStatus();
        long duration;
        try {
            duration = System.currentTimeMillis() - Long.parseLong(MDC.get(REQUEST_START_TIME));
        } catch (NumberFormatException e) {
            duration = -1;
        }

        if (exception != null) {
            log.error("Request failed: {} {} [status={}, duration={}ms, id={}]",
                requestMethod, requestUri, status, duration, requestId, exception);
        } else {
            log.info("Request completed: {} {} [status={}, duration={}ms, id={}]",
                requestMethod, requestUri, status, duration, requestId);
        }

        MDC.clear();
    }
}
