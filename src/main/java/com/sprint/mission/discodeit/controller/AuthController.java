package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import static com.sprint.mission.discodeit.security.jwt.JwtTokenProvider.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    UserDto userDto = authService.updateRole(request);
    return ResponseEntity.status(200).body(userDto);
  }

  @PostMapping("refresh")
  public ResponseEntity<JwtDto> refresh(@CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
                                        HttpServletResponse response) {
    log.info("토큰 리프레시 요청");
    JwtInformation refreshResult = authService.refreshToken(refreshToken);
    Cookie refreshCookie = jwtTokenProvider.genereateRefreshTokenCookie(
            refreshResult.getRefreshToken());
    response.addCookie(refreshCookie);

    JwtDto body = new JwtDto(
            refreshResult.getUserDto(),
            refreshResult.getAccessToken()
    );
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(body);
  }
}