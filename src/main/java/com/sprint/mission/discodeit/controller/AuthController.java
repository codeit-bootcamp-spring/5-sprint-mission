package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthDto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<UserDto.DetailResponse> login(@RequestBody LoginRequest request) {
    // TODO 나중에 로그인 세션 처리
    return ResponseEntity.ok(authService.login(request.toLogin()).toResponse());
  }
}
