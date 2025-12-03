package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.AuthControllerDocs;
import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwt.data.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.event.audit.AuthAuditPublisher;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
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

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthAuditPublisher authAuditPublisher;

    @GetMapping("/csrf-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getCsrfToken(CsrfToken csrfToken) {
    }

    @PostMapping("/refresh")
    public JwtDto refresh(
        @CookieValue("REFRESH_TOKEN") String refreshToken,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        JwtInformation jwtInformation = authService.refreshToken(refreshToken);

        Cookie refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(jwtInformation.refreshToken());
        response.addCookie(refreshCookie);

        UserDto userDto = userService.findById(jwtInformation.userDetailsDto().id());

        authAuditPublisher.logTokenRefresh(userDto.id(), userDto.username(), request);

        return new JwtDto(userDto, jwtInformation.accessToken());
    }

    @PutMapping("/role")
    public UserDto updateRole(@RequestBody RoleUpdateRequest request) {
        return authService.updateRole(request);
    }
}
