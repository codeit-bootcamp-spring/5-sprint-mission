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

    private static final String MDC_REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_METHOD_KEY = "requestMethod";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String REQUEST_START_TIME_KEY = "requestStartTime";
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

        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        MDC.put(REQUEST_METHOD_KEY, requestMethod);
        MDC.put(REQUEST_URI_KEY, requestUri);
        MDC.put(REQUEST_START_TIME_KEY, String.valueOf(System.currentTimeMillis()));

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
        String requestId = MDC.get(MDC_REQUEST_ID_KEY);
        String requestMethod = MDC.get(REQUEST_METHOD_KEY);
        String requestUri = MDC.get(REQUEST_URI_KEY);
        int status = response.getStatus();
        long duration;
        try {
            duration = System.currentTimeMillis() - Long.parseLong(MDC.get(REQUEST_START_TIME_KEY));
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
