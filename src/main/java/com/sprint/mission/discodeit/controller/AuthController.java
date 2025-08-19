package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

  private final BasicAuthService authService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<User> login(@RequestPart UserLoginRequest userLoginRequest) {
    User user = authService.login(userLoginRequest);

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}
