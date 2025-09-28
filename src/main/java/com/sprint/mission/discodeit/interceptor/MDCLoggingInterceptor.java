package com.sprint.mission.discodeit.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_METHOD = "requestMethod";
    private static final String REQUEST_URL = "requestUrl";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URL, request.getRequestURI());

        // 응답 헤더에 Request ID 추가
        response.addHeader("Discodeit-Request-ID", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        MDC.clear(); // 요청 완료 후 MDC 클리어
    }
}
