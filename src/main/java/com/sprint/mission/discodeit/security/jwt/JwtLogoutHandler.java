package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        Cookie expiredCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
        response.addCookie(expiredCookie);

        log.debug("JWT 로그아웃 핸들러 실행 - 리프레시 토큰 쿠키 삭제 완료");
    }
}
