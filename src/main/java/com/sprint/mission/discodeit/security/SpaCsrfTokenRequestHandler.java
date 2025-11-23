package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

// CSR + SPA 환경에서 사용할 CSRF 처리기.

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  // 헤더에서 읽을 때는 plain handler (원본 값)
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();

  // 그 외(파라미터 등)는 기존 XOR 핸들러
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {
    // 응답 바디에 토큰을 렌더링할 때 BREACH 공격을 방지.
    this.xor.handle(request, response, csrfToken);

    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    // 1) 헤더에 값이 있으면 plain handler 사용
    // 2) 그 외(요청 파라미터 등)는 XOR handler 사용
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor)
        .resolveCsrfTokenValue(request, csrfToken);
  }
}