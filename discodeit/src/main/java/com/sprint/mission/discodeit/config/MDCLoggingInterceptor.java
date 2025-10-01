package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * MDC(Mapped Diagnostic Context)
 * 요청마다 MDC에 컨텍스트 정보를 추가하는 인터셉터
 * Interceptor : URL 기준으로 Spring 레벨에서 필터를 제공해주는 인터페이스
 */
@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

    // MDC 로깅에 사용되는 상수 정의
    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_METHOD = "requestMethod";
    public static final String REQUEST_URI = "requestUri";
    public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

    // 요청 이전에 받아서 처리하는 핸들러
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 ID 생성 (IP + UUID 일부)
        String requestId = getClientIp(request) + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);

        // MDC에 컨텍스트 정보 추가
        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());

        // 응답 헤더에 요청 ID 추가
        response.setHeader(REQUEST_ID_HEADER, requestId);

        log.debug("Request started");

        return true;
    }

    // 요청 이후에 받아서 처리하는 핸들러
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//    }

    // 요청을 완료하고 나서 호출되는 핸들러
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("Request finished");
        // 요청 처리 후 MDC 데이터 정리
        MDC.clear();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            if (inetAddress.isLoopbackAddress()) {
                return "127.0.0.1";
            }
            if (inetAddress instanceof Inet6Address && ip.startsWith("::ffff:")) {
                return ip.substring(7);
            }
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return ip;
        }
    }
}
