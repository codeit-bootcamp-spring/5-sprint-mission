package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String REQUEST_METHOD_MDC_KEY = "requestMethod";
    private static final String REQUEST_URI_MDC_KEY = "requestURI";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestId = UUID.randomUUID().toString();

        // MDC 에 요청 정보 저장
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put(REQUEST_METHOD_MDC_KEY, request.getMethod());
        MDC.put(REQUEST_URI_MDC_KEY, request.getRequestURI());


        response.addHeader(REQUEST_ID_HEADER, requestId); // 응답 헤더에도 요청 ID 추가 (postHandle 에서도 가능)

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear(); // 요청 처리 완료 후 MDC 정보 제거
    }
}