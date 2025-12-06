package com.sprint.mission.discodeit.global.security.jwt;

import com.sprint.mission.discodeit.domain.auth.aop.annotation.MeasureLoginDuration;
import com.sprint.mission.discodeit.domain.auth.aop.aspect.LoginDurationAspect;
import com.sprint.mission.discodeit.domain.auth.domain.event.LoginFailureEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.JwtDto;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.response.JwtResponse;
import com.sprint.mission.discodeit.domain.user.application.UserService;
import com.sprint.mission.discodeit.domain.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtCookieProvider cookieProvider;
    private final JwtResponseWriter responseWriter;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtRegistry jwtRegistry;
    private final UserService userService;

    @Override
    @MeasureLoginDuration
    public void onAuthenticationSuccess(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Authentication authentication
    ) throws IOException {
        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            log.error("인증 실패: 예상치 못한 Principal 타입");
            return;
        }

        long duration = LoginDurationAspect.calculateDuration(request);

        try {
            JwtDto jwtDto = generateAndRegisterTokens(userDetails);
            UserDto userDto = userService.findById(userDetails.getUserDetailsDto().id());

            response.addCookie(cookieProvider.createRefreshTokenCookie(jwtDto.refreshToken()));
            responseWriter.writeSuccess(response, new JwtResponse(userDto, jwtDto.accessToken()));

            eventPublisher.publishEvent(new LoginSuccessEvent(
                userDto.id(),
                userDto.username(),
                extractIpAddress(request),
                extractUserAgent(request),
                duration
            ));

            log.info("JWT 토큰 발급 완료: username={}", userDetails.getUsername());
        } catch (Exception e) {
            log.error("JWT 토큰 생성 실패: username={}", userDetails.getUsername(), e);

            DiscodeitException exception = new DiscodeitException(ErrorCode.JWT_GENERATION_FAILED, e);
            responseWriter.writeError(response, exception);

            eventPublisher.publishEvent(new LoginFailureEvent(
                userDetails.getUsername(),
                extractIpAddress(request),
                extractUserAgent(request),
                e.getMessage(),
                duration
            ));
        }
    }

    private JwtDto generateAndRegisterTokens(DiscodeitUserDetails userDetails) {
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        JwtDto jwtDto = new JwtDto(
            userDetails.getUserDetailsDto(),
            accessToken,
            refreshToken
        );

        jwtRegistry.registerJwtInformation(jwtDto);
        return jwtDto;
    }
}
