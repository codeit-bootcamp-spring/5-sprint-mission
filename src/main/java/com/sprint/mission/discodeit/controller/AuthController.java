package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthService authService;

  @Operation(summary = "csrf token 발급")
  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(203)
                         .build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto.JwtResponse> refresh(
      @CookieValue("REFRESH_TOKEN") String refreshToken,
      HttpServletResponse response) {
    JwtDto.JwtResponse jwtDto = authService.refresh(refreshToken, response);
    return ResponseEntity.ok(jwtDto);
  }

  @Operation(summary = "사용자 권한 수정")
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<Void> updateRole(@RequestBody UserDto.UpdateRoleRequest request) {

    userService.update(UserDto.UpdateCommand.builder()
                                            .id(request.getUserId())
                                            .role(request.getNewRole())
                                            .build());

    return ResponseEntity.ok()
                         .build();
  }
}