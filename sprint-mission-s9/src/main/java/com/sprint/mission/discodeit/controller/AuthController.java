package com.sprint.mission.discodeit.controller;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserService userService;

  // CSRF 토큰 발급
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String token = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", token);
    return ResponseEntity.noContent().build();
  }

  // 리프레시 토큰이 새 액세스 토큰 발급
  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request, HttpServletResponse response) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);

      if (refreshToken == null) {
        log.debug("Refresh token not found in cookies");
        throw new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
      }

      if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
        log.debug("Invalid or expired refresh token");
        throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
      }

      if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
        log.debug("Refresh token not found in registry");
        throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
      }

      String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
      UUID userId = jwtTokenProvider.getUserId(refreshToken);

      List<String> roles = userService.getUserRoles(userId);

      String newAccessToken = jwtTokenProvider.generateAccessToken(username, userId, roles);

      String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, userId);
      Cookie newRefreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(newRefreshToken);
      response.addCookie(newRefreshTokenCookie);

      UserDto userDto = new UserDto(userId, username, null, null, null, null);
      JwtInformation newJwtInformation = new JwtInformation(
          userDto,
          newAccessToken,
          newRefreshToken
      );

      jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

      JwtDto jwtDto = new JwtDto(
          newAccessToken,
          jwtTokenProvider.getAccessTokenExpirationMs()
      );

      log.info("Access token refreshed for user: {}", username);
      return ResponseEntity.ok(jwtDto);

    } catch (InvalidRefreshTokenException e) {
      throw e;
    } catch (JOSEException e) {
      log.error("Failed to refresh access token", e);
      throw new InvalidRefreshTokenException(ErrorCode.TOKEN_GENERATION_FAILED);
    } catch (Exception e) {
      log.error("Unexpected error during token refresh", e);
      throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
  }

  // 리프레시 토큰 추출
  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies == null) {
      return null;
    }

    return Arrays.stream(cookies)
        .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }

  // 권한 업데이트
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    UserDto userDto = authService.updateRole(request);
    return ResponseEntity.ok(userDto);
  }
}