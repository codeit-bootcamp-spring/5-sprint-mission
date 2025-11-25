package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        try {
            // UserDetails에서 정보 추출
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            UUID userId = userDetails.getUserDto().id();

            // 권한 목록 추출
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            // Access Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(username, userId, roles);

            // Refresh Token 생성
            String refreshToken = jwtTokenProvider.generateRefreshToken(username, userId);

            // Refresh Token을 쿠키에 저장
            Cookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);
            response.addCookie(refreshTokenCookie);

            // Access Token을 응답 Body에 포함
            JwtDto jwtDto = new JwtDto(
                accessToken,
                jwtTokenProvider.getAccessTokenExpirationMs()
            );

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(response.getWriter(), jwtDto);

            log.info("JWT tokens issued for user: {}", username);

        } catch (JOSEException e) {
            log.error("Failed to generate JWT tokens", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"토큰 생성에 실패했습니다.\"}");
        }
    }
}