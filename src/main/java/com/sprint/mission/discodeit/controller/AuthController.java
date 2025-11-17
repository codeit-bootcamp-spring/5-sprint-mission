package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.request.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build(); // 203
    }

    @GetMapping("me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.info("현재 사용자 정보 조회: {}", userDetails.getUsername());
        return ResponseEntity.ok(userDetails.getUserDto());
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateUserRole(
            @Valid @RequestBody UserRoleUpdateRequest request) {
        log.info("[Controller] 권한 수정 요청: {}", request);
        UserDto userDto = authService.updateUserRole(request);
        return ResponseEntity.ok(userDto);
    }
}
