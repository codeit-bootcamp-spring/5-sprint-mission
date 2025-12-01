package com.sprint.mission.discodeit.controller;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry<UUID> jwtRegistry;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refreshToken(
          @CookieValue(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
          HttpServletResponse response
  ) {
    log.info("Refresh Token: {}", refreshToken);
    JwtInformation jwtInformation = authService.refreshToken(refreshToken);
    Cookie cookie = jwtTokenProvider.genereateRefreshTokenCookie(jwtInformation.getRefreshToken());
    response.addCookie(cookie);

    JwtDto body = new JwtDto(jwtInformation.getUserDto(), jwtInformation.getAccessToken());
    return ResponseEntity.ok(body);

  }


  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@RequestHeader("Authorization") String authHeader) {
    log.info("내 정보 조회 요청");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new RuntimeException("Access Token 누락");
    }

    String accessToken = authHeader.substring(7);

    // 1. Access Token 검증
    if (!jwtTokenProvider.validateAccessToken(accessToken)) {
      throw new RuntimeException("유효하지 않은 Access Token");
    }

    // 2. Access Token에서 userId 추출
    UUID userId = jwtTokenProvider.getUserId(accessToken);

    // 3. JwtRegistry에서 로그인 여부 확인
    boolean loggedIn = jwtRegistry.hasActiveJwtInformationByUserId(userId);
    if (!loggedIn) {
      throw new RuntimeException("로그아웃된 사용자입니다.");
    }

    // 4. User 정보 조회
    UserDto userDto = userService.find(userId);

    return ResponseEntity.ok(userDto);
  }


  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}
