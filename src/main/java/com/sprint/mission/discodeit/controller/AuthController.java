package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @PostMapping(path = "login")
  public ResponseEntity<UserDto> login(@Validated @RequestBody LoginRequest loginRequest) {
    log.info("로그인 요청 수신: username={}", loginRequest.username());

    try {
      UserDto user = authService.login(loginRequest);
      log.info("로그인 성공: username={}, userId={}", loginRequest.username(), user.id());
      return ResponseEntity.status(HttpStatus.OK).body(user);

    } catch (IllegalArgumentException e) {
      // 잘못된 비밀번호 같은 인증 실패
      log.warn("로그인 실패(잘못된 비밀번호): username={}", loginRequest.username());
      throw e;

    } catch (Exception e) {
      // 시스템 오류 (DB 등)
      log.error("로그인 처리 중 예외 발생: username={}", loginRequest.username(), e);
      throw e;
    }
  }
}