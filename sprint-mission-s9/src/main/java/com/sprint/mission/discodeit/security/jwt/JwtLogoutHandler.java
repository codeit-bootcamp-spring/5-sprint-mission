package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        Cookie expiredCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(expiredCookie);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .ifPresent(cookie -> {
                    try {
                        String refreshToken = cookie.getValue();

                        if (refreshToken != null && !refreshToken.isEmpty()) {
                            UUID userId = jwtTokenProvider.getUserId(refreshToken);

                            jwtRegistry.invalidateJwtInformationByUserId(userId);

                            log.info("User logged out and JWT information invalidated: {}", userId);
                        }
                    } catch (Exception e) {
                        log.debug("Failed to invalidate JWT information during logout: {}", e.getMessage());
                    }
                });
        }

        if (authentication != null && authentication.getName() != null) {
            log.info("User logged out: {}", authentication.getName());
        } else {
            log.debug("Logout handler executed (no authenticated user)");
        }
    }
}