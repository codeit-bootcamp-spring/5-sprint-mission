package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_METHOD_KEY = "requestMethod";
    private static final String REQUEST_URL_KEY = "requestUrl";
    private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        String requestId = UUID.randomUUID().toString();
        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestURI();

        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(REQUEST_METHOD_KEY, requestMethod);
        MDC.put(REQUEST_URL_KEY, requestUrl);

        response.setHeader(REQUEST_ID_HEADER, requestId);

        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        Exception ex
    ) {
        MDC.clear();
    }
}
