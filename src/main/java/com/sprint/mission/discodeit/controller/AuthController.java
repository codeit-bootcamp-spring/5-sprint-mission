package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.AuthControllerDocs;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/csrf-token")
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    public void getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰: {}", csrfToken.getToken());
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDto me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.info("내 정보 조회 요청");
        UUID userId = userDetails.getUserDto().id();
        return userService.find(userId);
    }

    @PutMapping("role")
    public UserDto updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        return authService.updateRole(request);
    }
}
