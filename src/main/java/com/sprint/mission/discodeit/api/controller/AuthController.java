package com.sprint.mission.discodeit.api.controller;

import com.sprint.mission.discodeit.api.aop.annotation.AuditRefresh;
import com.sprint.mission.discodeit.api.docs.AuthControllerDocs;
import com.sprint.mission.discodeit.common.security.jwt.JwtCookieProvider;
import com.sprint.mission.discodeit.domain.auth.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.auth.service.AuthService;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtDto;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.user.dto.data.UserDto;
import com.sprint.mission.discodeit.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final UserService userService;
    private final JwtCookieProvider cookieProvider;

    @GetMapping("/csrf-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getCsrfToken(CsrfToken csrfToken) {
    }

    @PostMapping("/refresh")
    @AuditRefresh
    public JwtDto refresh(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        JwtInformation jwtInformation = authService.refreshToken(request);

        response.addCookie(cookieProvider.createRefreshTokenCookie(jwtInformation.refreshToken()));

        UserDto userDto = userService.findById(jwtInformation.userDetailsDto().id());
        return new JwtDto(userDto, jwtInformation.accessToken());
    }

    @PutMapping("/role")
    public UserDto updateRole(@RequestBody RoleUpdateRequest request) {
        return authService.updateRole(request);
    }
}
