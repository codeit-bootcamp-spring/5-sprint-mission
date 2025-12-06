package com.sprint.mission.discodeit.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import static org.springframework.util.StringUtils.hasText;

public final class RequestExtractor {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final int USER_AGENT_MAX_LENGTH = 500;

    private RequestExtractor() {
    }

    public static String extractIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    public static String extractUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);

        if (userAgent != null && userAgent.length() > USER_AGENT_MAX_LENGTH) {
            return userAgent.strip().substring(0, USER_AGENT_MAX_LENGTH);
        }

        return userAgent;
    }
}
