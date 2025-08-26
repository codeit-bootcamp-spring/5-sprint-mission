package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @RequestMapping(path = "/login", method = RequestMethod.POST)
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    try {
      LoginResponse login = authService.login(loginRequest);
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(login);
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
  }
}
