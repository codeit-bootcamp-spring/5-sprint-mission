package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jboss.logging.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID = "requestId";
  private static final String REQUEST_METHOD = "requestMethod";
  private static final String REQUEST_URI = "requestURI";
  private static final String REQUEST_ID_HEADER = "requestId";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String requestId =
        getClientIp(request) + "-" + java.util.UUID.randomUUID().toString().substring(0, 0);

    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URI, request.getRequestURI());

    response.setHeader(REQUEST_ID_HEADER, requestId);

    return true;
  }

  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception e) {
    MDC.clear();
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("Discodeit-Request-ID");
    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      ip = ip.split(",")[0].trim();
    } else {
      ip = request.getRemoteAddr();
    }
    try {
      InetAddress inet = InetAddress.getByName(ip);
      if (inet.isLoopbackAddress()) {
        return "127.0.0.1";
      }
      if (inet instanceof Inet6Address && ip.startsWith("::ffff:")) {
        return ip.substring(7);
      }
      return inet.getHostAddress();
    } catch (UnknownHostException e) {
      return ip;
    }
  }
}

