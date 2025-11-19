package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.config.security.login.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
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

    private final AuthService authService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal DiscodeitUserDetails user) {
        return UserDto.builder()
                .id(user.getUserDto().id())
                .username(user.getUsername())
                .email(user.getUserDto().email())
                .profile(user.getUserDto().profile())
                .online(user.getUserDto().online())
                .build();
    }

    @PutMapping("/role")
    public UserDto updateRole(@RequestBody UserRoleUpdateRequest request) {
        return authService.updateUserRole(request.userId(), request.role());
    }

}
