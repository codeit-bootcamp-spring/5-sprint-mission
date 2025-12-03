package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;

    // Spring Security가 로그아웃 시 호출
    @Override
    @SneakyThrows
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 로그아웃 시 요청 refreshToken 값을 바탕으로 JWT 세션 및 토큰 쿠키 무효화
        resolveRefreshToken(request)
                .ifPresent(refreshToken -> {
                    jwtService.invalidateJwtSession(refreshToken);
                    invalidateRefreshTokenCookie(response);
                });
    }

    // 요청에서 refreshTokenCookie만 찾아 Optional로 리턴
    private Optional<String> resolveRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtService.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .map(Cookie::getValue);
    }

    // 기존 refreshToken 쿠키 제거
    private void invalidateRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(JwtService.REFRESH_TOKEN_COOKIE_NAME, "");

        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);
    }
}
