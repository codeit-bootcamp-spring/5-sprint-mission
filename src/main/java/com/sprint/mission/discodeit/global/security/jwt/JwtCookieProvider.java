package com.sprint.mission.discodeit.global.security.jwt;

import com.sprint.mission.discodeit.global.config.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JwtCookieProvider {

    private final String refreshTokenCookieName;
    private final Duration refreshTokenExpiration;

    public JwtCookieProvider(JwtProperties jwtProperties) {
        this.refreshTokenCookieName = jwtProperties.refreshTokenCookieName();
        this.refreshTokenExpiration = jwtProperties.refreshToken().expiration();
    }

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenExpiration.toSeconds());
        return cookie;
    }

    public Cookie createExpiredRefreshTokenCookie() {
        Cookie cookie = new Cookie(refreshTokenCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
