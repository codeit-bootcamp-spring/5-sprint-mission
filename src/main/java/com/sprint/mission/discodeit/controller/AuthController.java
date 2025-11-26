package com.sprint.mission.discodeit.controller;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.docs.AuthControllerDocs;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @GetMapping("/csrf-token")
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    public void getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰: {}", csrfToken.getToken());
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public JwtDto refresh(HttpServletRequest request) throws JOSEException {
        log.debug("토큰 재발급 요청");

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new InvalidTokenException();
        }

        String refreshToken = Arrays.stream(cookies)
            .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElseThrow(InvalidTokenException::new);

        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        DiscodeitUserDetails userDetails =
            (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

        String accessToken = tokenProvider.generateAccessToken(userDetails);

        log.info("토큰 재발급 완료: {}", username);
        return new JwtDto(userDetails.getUserDto(), accessToken);
    }

    @PutMapping("role")
    public UserDto updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        return authService.updateRole(request);
    }
}
