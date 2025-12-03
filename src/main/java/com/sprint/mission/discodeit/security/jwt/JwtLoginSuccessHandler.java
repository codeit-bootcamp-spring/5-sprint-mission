package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.jwt.data.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.event.audit.AuthAuditPublisher;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthMetricsService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final AuthAuditPublisher authAuditPublisher;
    private final AuthMetricsService authMetricsService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Authentication authentication
    ) throws IOException, ServletException {

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            log.error("인증 실패: 예상치 못한 Principal 타입");
            return;
        }

        try {
            String accessToken = tokenProvider.generateAccessToken(userDetails);
            String refreshToken = tokenProvider.generateRefreshToken(userDetails);

            Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(refreshToken);
            response.addCookie(refreshCookie);

            UserDto userDto = userService.findById(userDetails.getUserDetailsDto().id());

            JwtDto jwtDto = new JwtDto(
                userDto,
                accessToken
            );

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

            JwtInformation jwtInformation = new JwtInformation(
                userDetails.getUserDetailsDto(),
                accessToken,
                refreshToken
            );
            jwtRegistry.registerJwtInformation(jwtInformation);

            authAuditPublisher.logLoginSuccess(
                userDetails.getUserDetailsDto().id(),
                userDetails.getUsername(),
                request
            );
            authMetricsService.recordLoginAttempt(true);

            log.info("JWT 토큰 발급 완료: username={}", userDetails.getUsername());
        } catch (JOSEException e) {
            log.error("JWT 토큰 생성 실패: username={}", userDetails.getUsername(), e);

            DiscodeitException exception = new DiscodeitException(ErrorCode.JWT_GENERATION_FAILED, e);
            ErrorResponse errorResponse = ErrorResponse.from(exception);

            response.setStatus(exception.getErrorCode().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
