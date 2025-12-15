package com.sprint.mission.discodeit.global.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String RATE_LIMIT_RETRY_AFTER_HEADER = "Retry-After";

    private final LoginRateLimitRegistry loginRateLimitRegistry;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!isLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = extractIpAddress(request);

        if (loginRateLimitRegistry.isBlocked(clientKey)) {
            handleBlockedRequest(response, clientKey);
            return;
        }

        loginRateLimitRegistry.recordAttempt(clientKey);

        int remaining = loginRateLimitRegistry.getRemainingAttempts(clientKey);
        response.setHeader(RATE_LIMIT_REMAINING_HEADER, String.valueOf(remaining));

        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpStatus.OK.value()) {
            loginRateLimitRegistry.resetAttempts(clientKey);
        }
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return LOGIN_PATH.equals(request.getRequestURI())
            && "POST".equalsIgnoreCase(request.getMethod());
    }

    private void handleBlockedRequest(HttpServletResponse response, String clientKey) throws IOException {
        long retryAfter = loginRateLimitRegistry.getBlockedSecondsRemaining(clientKey);

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(RATE_LIMIT_REMAINING_HEADER, "0");
        response.setHeader(RATE_LIMIT_RETRY_AFTER_HEADER, String.valueOf(retryAfter));

        ErrorResponse errorResponse = ErrorResponse.of(
            ErrorCode.TOO_MANY_REQUESTS.name(),
            ErrorCode.TOO_MANY_REQUESTS.getMessage(),
            Map.of("retryAfterSeconds", retryAfter),
            null,
            HttpStatus.TOO_MANY_REQUESTS
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        log.warn("Rate limit blocked request from {}, retry after {} seconds", clientKey, retryAfter);
    }
}
