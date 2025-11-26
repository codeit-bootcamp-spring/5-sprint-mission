package com.sprint.mission.discodeit.controller;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.docs.AuthControllerDocs;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.audit.AuthAuditService;
import com.sprint.mission.discodeit.service.audit.AuthMetricsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final AuthAuditService authAuditService;
    private final AuthMetricsService authMetricsService;

    @GetMapping("/csrf-token")
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    public void getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰: {}", csrfToken.getToken());
    }

    @PostMapping("/refresh")
    public JwtDto refresh(
        @CookieValue("REFRESH_TOKEN") String refreshToken,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws JOSEException {
        log.debug("토큰 재발급 요청");

        JwtInformation jwtInformation = authService.refreshToken(refreshToken);

        Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(jwtInformation.getRefreshToken());
        response.addCookie(refreshCookie);

        JwtDto jwtDto = new JwtDto(
            jwtInformation.getUserDto(),
            jwtInformation.getAccessToken()
        );

        authAuditService.logTokenRefresh(
            jwtInformation.getUserDto().id(),
            jwtInformation.getUserDto().username(),
            request
        );
        authMetricsService.recordTokenRefreshSuccess();

        log.info("토큰 재발급 완료 (Rotation 적용): {}", jwtInformation.getUserDto().username());

        return jwtDto;
    }

    @PutMapping("/role")
    public UserDto updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        return authService.updateRole(request);
    }
}
