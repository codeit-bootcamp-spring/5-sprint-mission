package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final UserMapper userMapper;

  @Operation(summary = "csrf token 발급")
  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(203)
                         .build();
  }

  @Operation(summary = "로그인 사용자 정보 조회")
  @GetMapping("/me")
  public ResponseEntity<UserDto.DetailResponse> getCurrentUser(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                           .build();
    }

    return ResponseEntity.ok(
        userMapper.toDetailResponse(userDetails.getUserDetail()));
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