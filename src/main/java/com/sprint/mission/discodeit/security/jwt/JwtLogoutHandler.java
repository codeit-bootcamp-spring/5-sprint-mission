package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // REFRESH_TOKEN 쿠키 조회
    Optional<Cookie> refreshCookie = Optional.ofNullable(request.getCookies())
                                             .flatMap(cookies ->
                                                 Arrays.stream(cookies)
                                                       .filter(
                                                           c -> "REFRESH_TOKEN".equals(c.getName()))
                                                       .findFirst()
                                             );

    // 쿠키 제거
    refreshCookie.ifPresent(cookie -> {
      cookie.setValue("");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    });

    // Refresh Token 기반으로 Registry 무효화
    refreshCookie.map(Cookie::getValue)
                 .map(jwtTokenProvider::getUserIdFromRefreshToken)
                 .ifPresent(jwtRegistry::invalidateJwtInformationByUserId);

    // 인증 정보 기반 무효화 (추가 안전 장치)
    if (authentication != null
        && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      jwtRegistry.invalidateJwtInformationByUserId(userDetails.getId());
    }
  }
}