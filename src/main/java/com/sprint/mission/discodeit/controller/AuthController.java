package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;

  // === CSRF 토큰 발급 API ===
  @Override
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: token={}", tokenValue);

    // 203 Non-Authoritative Information
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }

  // === 세션 기반 현재 사용자 정보 조회 API ===
  @Override
  @GetMapping("/me")
  public ResponseEntity<UserDto> me(
      @AuthenticationPrincipal DiscodeitUserDetails principal
  ) {
    if (principal == null) {
      log.warn("인증 정보 없이 /api/auth/me 호출됨");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UserDto userDto = principal.getUserDto();
    log.debug("현재 사용자 조회: id={}, username={}",
        userDto.id(), userDto.username());

    return ResponseEntity.ok(userDto);
  }

  // === 사용자 권한 변경 API ===
  @Override
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(
      @RequestBody @Valid UserRoleUpdateRequest request
  ) {
    log.info("사용자 권한 변경 요청: userId={}, newRole={}",
        request.userId(), request.newRole());

    UserDto updated = userService.updateRole(request);

    log.debug("사용자 권한 변경 완료: id={}, newRole={}",
        updated.id(), updated.role());

    return ResponseEntity.ok(updated);
  }
}