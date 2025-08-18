package com.sprint.mission.discodeit.controller.auth;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.request.auth.AuthLogoutRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping(path = "/login")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse login(@RequestBody AuthLoginRequest body) {
    return authService.login(body);
  }

  @PostMapping(path = "/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@Valid @RequestBody AuthLogoutRequest body) {
    authService.logout(body.userId());
  }
}
