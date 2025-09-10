package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserMapper userMapper;

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
    User user = authService.login(loginRequest);

    UserDto dto = userMapper.toDto(user, true);
    return ResponseEntity.ok(dto);
  }
}
