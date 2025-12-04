package com.sprint.mission.discodeit.domain.controller;

import com.sprint.mission.discodeit.common.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.common.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.domain.docs.AuthControllerDocs;
import com.sprint.mission.discodeit.domain.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtDto;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.service.AuthService;
import com.sprint.mission.discodeit.domain.service.UserService;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshSuccessEvent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractUserAgent;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/csrf-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getCsrfToken(CsrfToken csrfToken) {
    }

    @PostMapping("/refresh")
    public JwtDto refresh(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            String cookieName = jwtTokenProvider.getRefreshTokenCookieName();
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            Assert.notNull(cookie, "Cookie not found");
            String refreshToken = cookie.getValue();

            JwtInformation jwtInformation = authService.refreshToken(refreshToken);

            Cookie refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(jwtInformation.refreshToken());
            response.addCookie(refreshCookie);

            UserDto userDto = userService.findById(jwtInformation.userDetailsDto().id());

            eventPublisher.publishEvent(new TokenRefreshSuccessEvent(
                userDto.id(),
                userDto.username(),
                extractIpAddress(request),
                extractUserAgent(request)
            ));

            return new JwtDto(userDto, jwtInformation.accessToken());
        } catch (InvalidTokenException e) {
            eventPublisher.publishEvent(new TokenRefreshFailureEvent(
                null,
                null,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                e.getMessage()
            ));
            throw e;
        }
    }

    @PutMapping("/role")
    public UserDto updateRole(@RequestBody RoleUpdateRequest request) {
        return authService.updateRole(request);
    }
}
