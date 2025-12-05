package com.sprint.mission.discodeit.common.security.jwt;

import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.auth.event.LoginFailureEvent;
import com.sprint.mission.discodeit.domain.auth.event.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtDto;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.user.dto.data.UserDto;
import com.sprint.mission.discodeit.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.common.util.RequestExtractor.extractUserAgent;

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
    public void onAuthenticationSuccess(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Authentication authentication
    ) throws IOException {
        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            log.error("인증 실패: 예상치 못한 Principal 타입");
            return;
        }

        long startTime = Long.parseLong(MDC.get("requestStartTime"));
        long duration = System.currentTimeMillis() - startTime;

        try {
            JwtInformation jwtInformation = generateAndRegisterTokens(userDetails);
            UserDto userDto = userService.findById(userDetails.getUserDetailsDto().id());

            response.addCookie(cookieProvider.createRefreshTokenCookie(jwtInformation.refreshToken()));
            responseWriter.writeSuccess(response, new JwtDto(userDto, jwtInformation.accessToken()));

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

    private JwtInformation generateAndRegisterTokens(DiscodeitUserDetails userDetails) {
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        JwtInformation jwtInformation = new JwtInformation(
            userDetails.getUserDetailsDto(),
            accessToken,
            refreshToken
        );

        jwtRegistry.registerJwtInformation(jwtInformation);
        return jwtInformation;
    }
}
