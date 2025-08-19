package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

  private final BasicAuthService authService;

  @Operation(summary = "로그인")
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<User> login(@RequestBody UserLoginRequest userLoginRequest) {
    User user = authService.login(userLoginRequest);

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}
