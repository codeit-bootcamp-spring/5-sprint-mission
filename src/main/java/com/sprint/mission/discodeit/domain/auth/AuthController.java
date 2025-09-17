package com.sprint.mission.discodeit.domain.auth;

import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.auth.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @PostMapping(path = "login")
  public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
    UserDto user = authService.login(loginRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
