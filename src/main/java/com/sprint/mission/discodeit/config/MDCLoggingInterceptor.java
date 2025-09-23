package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

  public static final String REQUEST_ID = "requestId";
  public static final String REQUEST_METHOD = "requestMethod";
  public static final String REQUEST_URI = "requestUri";
  public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";


  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);

    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URI, request.getRequestURI());

    response.setHeader(REQUEST_ID_HEADER, requestId);

    log.debug("Request started");

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    log.debug("Request finished");
    MDC.clear();
  }

}
