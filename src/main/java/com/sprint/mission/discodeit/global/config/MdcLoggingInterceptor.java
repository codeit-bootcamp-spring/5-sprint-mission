package com.sprint.mission.discodeit.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@Component
@Slf4j
public class MdcLoggingInterceptor implements HandlerInterceptor {

    private static final String KEY_REQUEST_ID = "requestId";
    private static final String KEY_REQUEST_METHOD = "requestMethod";
    private static final String KEY_REQUEST_URI = "requestUri";
    private static final String KEY_REQUEST_START_TIME = "requestStartTime";
    private static final String KEY_IP_ADDRESS = "ipAddress";
    private static final String KEY_USER_AGENT = "userAgent";
    private static final String HEADER_REQUEST_ID = "Discodeit-Request-ID";

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        String requestId = UUID.randomUUID().toString();
        String requestMethod = request.getMethod();
        String requestUri = request.getRequestURI();
        String requestStartTime = String.valueOf(System.currentTimeMillis());
        String ipAddress = extractIpAddress(request);
        String userAgent = extractUserAgent(request);

        MDC.put(KEY_REQUEST_ID, requestId);
        MDC.put(KEY_REQUEST_METHOD, requestMethod);
        MDC.put(KEY_REQUEST_URI, requestUri);
        MDC.put(KEY_REQUEST_START_TIME, requestStartTime);
        MDC.put(KEY_IP_ADDRESS, ipAddress);
        MDC.put(KEY_USER_AGENT, userAgent);

        response.setHeader(HEADER_REQUEST_ID, requestId);

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
        String requestId = MDC.get(KEY_REQUEST_ID);
        String requestMethod = MDC.get(KEY_REQUEST_METHOD);
        String requestUri = MDC.get(KEY_REQUEST_URI);
        int status = response.getStatus();
        long duration;
        try {
            duration = System.currentTimeMillis() - Long.parseLong(MDC.get(KEY_REQUEST_START_TIME));
        } catch (NumberFormatException e) {
            duration = -1;
        }

        if (exception != null) {
            log.error("Request failed: {} {} [status={}, duration={}ms, id={}]",
                requestMethod, requestUri, status, duration, requestId, exception);
        } else {
            log.debug("Request completed: {} {} [status={}, duration={}ms, id={}]",
                requestMethod, requestUri, status, duration, requestId);
        }

        clearMdcContext();
    }

    private void clearMdcContext() {
        MDC.remove(KEY_REQUEST_ID);
        MDC.remove(KEY_REQUEST_METHOD);
        MDC.remove(KEY_REQUEST_URI);
        MDC.remove(KEY_REQUEST_START_TIME);
        MDC.remove(KEY_IP_ADDRESS);
        MDC.remove(KEY_USER_AGENT);
    }
}
