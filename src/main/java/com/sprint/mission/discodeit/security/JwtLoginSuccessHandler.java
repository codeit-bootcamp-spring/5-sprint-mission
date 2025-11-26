package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.controller.advice.ErrorResponse;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidCredentialsException;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;

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

                Cookie refreshCookie = tokenProvider.genereateRefreshTokenCookie(refreshToken);
                response.addCookie(refreshCookie);

                JwtDto jwtDto = new JwtDto(
                    userDetails.getUserDto(),
                    accessToken
                );

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());
            } catch (JOSEException e) {

                log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ErrorCode errorCode = ErrorCode.JWT_GENERATION_FAILED;
                ErrorResponse errorResponse = ErrorResponse.of(
                    errorCode.name(),
                    errorCode.getMessage(),
                    Map.of(),
                    e,
                    errorCode.getHttpStatus()
                );
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            DiscodeitException exception = new InvalidCredentialsException();
            ErrorResponse errorResponse = ErrorResponse.of(
                exception.getErrorCode().name(),
                exception.getMessage(),
                Map.of(),
                exception,
                exception.getErrorCode().getHttpStatus()
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
