package com.sprint.mission.discodeit.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public final class RequestExtractor {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final int USER_AGENT_MAX_LENGTH = 500;

    private RequestExtractor() {
    }

    public static String extractIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        return Optional.ofNullable(request.getHeader(X_FORWARDED_FOR))
            .map(xff -> xff.split(",")[0].strip())
            .filter(ip -> !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip))
            .orElseGet(request::getRemoteAddr);
    }

    public static String extractUserAgent(HttpServletRequest request) {
        return Optional.ofNullable(request)
            .map(req -> req.getHeader(HttpHeaders.USER_AGENT))
            .map(ua -> ua.replaceAll("[\\r\\n\\t]", "").strip())
            .map(ua -> ua.length() > USER_AGENT_MAX_LENGTH
                ? ua.substring(0, USER_AGENT_MAX_LENGTH)
                : ua)
            .orElse(null);
    }
}
