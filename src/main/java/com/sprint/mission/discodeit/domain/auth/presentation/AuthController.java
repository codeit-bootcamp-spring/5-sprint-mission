package com.sprint.mission.discodeit.domain.auth.presentation;

import com.sprint.mission.discodeit.domain.auth.application.AuthService;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.JwtDto;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.response.JwtResponse;
import com.sprint.mission.discodeit.domain.user.application.UserService;
import com.sprint.mission.discodeit.domain.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.global.security.jwt.JwtCookieProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public JwtResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        JwtDto jwtDto = authService.refreshToken(request);
        response.addCookie(cookieProvider.createRefreshTokenCookie(jwtDto.refreshToken()));

        UserDto userDto = userService.findById(jwtDto.userDetailsDto().id());
        return new JwtResponse(userDto, jwtDto.accessToken());
    }

    @PutMapping("/role")
    public UserDto updateRole(@Valid @RequestBody RoleUpdateRequest request) {
        return authService.updateRole(request);
    }
}
