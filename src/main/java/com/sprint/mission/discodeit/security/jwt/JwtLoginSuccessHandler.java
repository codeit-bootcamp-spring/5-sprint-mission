package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.exception.base.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
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
import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      try {
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        // Set refresh token in HttpOnly cookie
        Cookie refreshCookie = tokenProvider.genereateRefreshTokenCookie(refreshToken);
        response.addCookie(refreshCookie);

        JwtDto jwtDto = new JwtDto(
            userDetails.getUserDto(),
            accessToken
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

        jwtRegistry.registerJwtInformation(
            new JwtInformation(
                userDetails.getUserDto(),
                accessToken,
                refreshToken
            )
        );

        log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());

      } catch (JOSEException e) {
        log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "TOKEN_GENERATION_FAILED",                  // code
                "JWT 토큰 생성 중 오류가 발생했습니다.",          // message
                Map.of("username", userDetails.getUsername()), // details
                e.getClass().getSimpleName(),                // exceptionType
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR // status
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
      }
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);

      ErrorResponse errorResponse = new ErrorResponse(
              Instant.now(),
              "AUTHENTICATION_FAILED",                    // code
              "사용자 인증에 실패했습니다: 유효하지 않은 사용자 정보입니다.", // message
              Map.of(),                                   // details (없을 경우 빈 Map)
              RuntimeException.class.getSimpleName(),     // exceptionType
              HttpServletResponse.SC_UNAUTHORIZED         // status
      );

      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
  }

}