package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.UUID;

/**
 * 요청마다 MDC에 컨텍스트 정보를 추가하는 인터셉터
 */
@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_METHOD = "requestMethod";
    public static final String REQUEST_URI = "requestUri";
    public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
    private static final String MDC_PREV_ATTR = "MDC_PREVIOUS";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 이전 MDC 맵 보관(있다면)
        Map<String, String> previous = MDC.getCopyOfContextMap();
        request.setAttribute(MDC_PREV_ATTR, previous);

        // 클라이언트가 제공한 Request-ID가 있으면 재사용
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().replaceAll("-", "");
        }

        // MDC 세팅
        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());

        // 응답 헤더에 Request-ID 노출(중복 설정 방지)
        if (response.getHeader(REQUEST_ID_HEADER) == null) {
            response.setHeader(REQUEST_ID_HEADER, requestId);
        }

        log.debug("Request started, requestId={}", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        log.debug("Request completed, requestId={}", MDC.get(REQUEST_ID));
        // 이전 MDC 맵 복구
        Object prev = request.getAttribute(MDC_PREV_ATTR);
        MDC.clear();
        if (prev instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> previous = (Map<String, String>) prev;
            MDC.setContextMap(previous);
        }
    }


//    /**
//     * MDC 로깅에 사용되는 상수 정의
//     */
//    public static final String REQUEST_ID = "requestId";
//    public static final String REQUEST_METHOD = "requestMethod";
//    public static final String REQUEST_URI = "requestUri";
//
//    public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        // 요청 ID 생성 (UUID)
//        String requestId = UUID.randomUUID().toString().replaceAll("-", "");
//
//        // MDC에 컨텍스트 정보 추가
//        MDC.put(REQUEST_ID, requestId);
//        MDC.put(REQUEST_METHOD, request.getMethod());
//        MDC.put(REQUEST_URI, request.getRequestURI());
//
//        // 응답 헤더에 요청 ID 추가
//        response.setHeader(REQUEST_ID_HEADER, requestId);
//
//        log.debug("Request started");
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        // 요청 처리 후 MDC 데이터 정리
//        log.debug("Request completed");
//        MDC.clear();
//    }
} 