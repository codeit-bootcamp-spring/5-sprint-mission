package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID_KEY = "requestId";
  private static final String METHOD_KEY = "method";
  private static final String URL_KEY = "url";
  private static final String RESPONSE_HEADER = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String requestId = UUID.randomUUID()
                           .toString();
    MDC.put(REQUEST_ID_KEY, requestId);
    MDC.put(METHOD_KEY, request.getMethod());
    MDC.put(URL_KEY, request.getRequestURI());

    // 응답 헤더에 Request-ID 추가
    response.addHeader(RESPONSE_HEADER, requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    MDC.clear();
  }
}