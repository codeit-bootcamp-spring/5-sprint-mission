package com.sprint.mission.discodeit.common.security.jwt;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtDto;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtCookieProvider cookieProvider;
    private final JwtResponseWriter responseWriter;
    private final JwtAuthEventPublisher eventPublisher;
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

        try {
            JwtInformation jwtInformation = generateAndRegisterTokens(userDetails);
            UserDto userDto = userService.findById(userDetails.getUserDetailsDto().id());

            response.addCookie(cookieProvider.createRefreshTokenCookie(jwtInformation.refreshToken()));
            responseWriter.writeSuccess(response, new JwtDto(userDto, jwtInformation.accessToken()));

            eventPublisher.publishLoginSuccess(userDto.id(), userDto.username(), request);
            log.info("JWT 토큰 발급 완료: username={}", userDetails.getUsername());
        } catch (Exception e) {
            log.error("JWT 토큰 생성 실패: username={}", userDetails.getUsername(), e);

            DiscodeitException exception = new DiscodeitException(ErrorCode.JWT_GENERATION_FAILED, e);
            responseWriter.writeError(response, exception);

            eventPublisher.publishLoginFailure(userDetails.getUsername(), e.getMessage(), request);
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
