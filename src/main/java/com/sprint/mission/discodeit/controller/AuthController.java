package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Auth", description = "인증/로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /*계층 분리
   * Controller : 요청 자체가 null인지 확인
   *   Service : 요청 내용이 올바른지 판단
   *  */

  @Operation(summary = "로그인")
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<User> login(@RequestBody @Valid LoginRequest request) {

    log.info("로그인 요청: {}", request);

    //값 자체가 NULL인지 확인
    if (request == null) {
      log.warn("로그인 요청이 null임");
      return ResponseEntity.badRequest().body(null); // or .build();
    }

    //서비스 호출
    User user = authService.login(request);
    return ResponseEntity.ok(user);
  }
}
