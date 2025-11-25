package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry<UUID> jwtRegistry;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Cookie refreshTokenCookie = jwtTokenProvider.genereateRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenCookie);

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();
                    UUID userId = jwtTokenProvider.getUserId(refreshToken);
                    // 토큰 무효화 리스트
                    jwtRegistry.invalidateJwtInformationByUserId(userId);
                });

        try{
        jwtRegistry.invalidateJwtInformationByUserId( ( (DiscodeitUserDetails)authentication.getPrincipal()).getUserDto().id());
        } catch (Exception e) {
        }

        log.debug("JWT logout handler executed - refresh token cookie cleared");

    }
}
