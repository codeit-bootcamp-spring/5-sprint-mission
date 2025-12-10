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

    Optional<Cookie> refreshCookie = Optional.ofNullable(request.getCookies())
                                             .flatMap(cookies ->
                                                 Arrays.stream(cookies)
                                                       .filter(
                                                           c -> "REFRESH_TOKEN".equals(c.getName()))
                                                       .findFirst()
                                             );

    refreshCookie.ifPresent(cookie -> {
      cookie.setValue("");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    });

    refreshCookie.map(Cookie::getValue)
                 .map(jwtTokenProvider::getUserIdFromRefreshToken)
                 .ifPresent(jwtRegistry::invalidateJwtInformationByUserId);

    if (authentication != null
        && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      jwtRegistry.invalidateJwtInformationByUserId(userDetails.getId());
    }
  }
}