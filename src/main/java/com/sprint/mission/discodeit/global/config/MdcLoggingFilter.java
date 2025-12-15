package com.sprint.mission.discodeit.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MdcLoggingFilter extends OncePerRequestFilter {

    public static final String KEY_REQUEST_ID = "requestId";
    public static final String KEY_REQUEST_METHOD = "requestMethod";
    public static final String KEY_REQUEST_URI = "requestUri";
    public static final String KEY_REQUEST_START_TIME = "requestStartTime";
    public static final String KEY_IP_ADDRESS = "ipAddress";
    public static final String KEY_USER_AGENT = "userAgent";
    public static final String HEADER_REQUEST_ID = "Discodeit-Request-ID";

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
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

        try {
            log.debug("Request started: {} {}", requestMethod, requestUri);
            filterChain.doFilter(request, response);
        } finally {
            logRequestCompletion(response);
            clearMdcContext();
        }
    }

    private void logRequestCompletion(HttpServletResponse response) {
        String requestId = MDC.get(KEY_REQUEST_ID);
        String requestMethod = MDC.get(KEY_REQUEST_METHOD);
        String requestUri = MDC.get(KEY_REQUEST_URI);
        int status = response.getStatus();
        String duration = calculateDuration();

        if (status >= 500) {
            log.error("Request failed: {} {} [status={}, duration={}, id={}]",
                requestMethod, requestUri, status, duration, requestId);
        } else if (status >= 400) {
            log.warn("Request completed: {} {} [status={}, duration={}, id={}]",
                requestMethod, requestUri, status, duration, requestId);
        } else {
            log.info("Request completed: {} {} [status={}, duration={}, id={}]",
                requestMethod, requestUri, status, duration, requestId);
        }
    }

    private String calculateDuration() {
        String startTimeStr = MDC.get(KEY_REQUEST_START_TIME);
        if (startTimeStr == null) {
            return "N/A";
        }
        try {
            long duration = System.currentTimeMillis() - Long.parseLong(startTimeStr);
            return duration + "ms";
        } catch (NumberFormatException e) {
            return "N/A";
        }
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
