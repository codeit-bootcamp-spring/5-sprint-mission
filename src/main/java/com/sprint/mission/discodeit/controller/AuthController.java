package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final UserService userService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(203).build();  // Body 없음
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(
          @AuthenticationPrincipal DiscodeitUserDetails user
  ) {
    if (user == null) {
      return ResponseEntity.status(401).build();
    }

    return ResponseEntity.status(200).body(user.getUserDto());
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    UserDto userDto = userService.updateRole(request);
    return ResponseEntity.status(200).body(userDto);
  }
}