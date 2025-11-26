package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.auth.InvalidCredentialsException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.audit.AuthAuditService;
import com.sprint.mission.discodeit.service.audit.AuthMetricsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!test")
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final AuthAuditService authAuditService;
    private final AuthMetricsService authMetricsService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {
                String accessToken = tokenProvider.generateAccessToken(userDetails);
                String refreshToken = tokenProvider.generateRefreshToken(userDetails);

                Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(refreshToken);
                response.addCookie(refreshCookie);

                JwtDto jwtDto = new JwtDto(
                    userDetails.getUserDto(),
                    accessToken
                );

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                JwtInformation jwtInformation = new JwtInformation(
                    userDetails.getUserDto(),
                    accessToken,
                    refreshToken
                );
                jwtRegistry.registerJwtInformation(jwtInformation);

                authAuditService.logLoginSuccess(
                    userDetails.getUserDto().id(),
                    userDetails.getUsername(),
                    request
                );
                authMetricsService.recordLoginSuccess();

                log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());
            } catch (JOSEException e) {
                log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);

                DiscodeitException exception = new DiscodeitException(ErrorCode.JWT_GENERATION_FAILED, e);
                ErrorResponse errorResponse = ErrorResponse.from(exception);

                response.setStatus(exception.getErrorCode().getHttpStatus().value());
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }

        } else {
            InvalidCredentialsException exception = new InvalidCredentialsException();
            ErrorResponse errorResponse = ErrorResponse.from(exception);

            response.setStatus(exception.getErrorCode().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
