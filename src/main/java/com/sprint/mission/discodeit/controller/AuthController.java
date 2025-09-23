package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthDto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
  private final UserMapper userMapper;

  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<UserDto.DetailResponse> login(@Valid @RequestBody LoginRequest request) {
    // TODO 나중에 로그인   세션 처리
    return ResponseEntity.ok(userMapper.toDetailResponse(authService.login(request.toLogin())));
  }
}
