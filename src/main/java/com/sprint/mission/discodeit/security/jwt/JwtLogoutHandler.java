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

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        Cookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenCookie);

        try {
            if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
                UUID userId = userDetails.getUserResponse().getId();
                jwtRegistry.invalidateJwtInformationByUserId(userId);
                log.info("로그아웃 성공: userId={}", userId);
            }
        } catch (Exception e) {
            log.warn("로그아웃 중 Registry 무효화 실패: {}", e.getMessage());
        }

        log.debug("JWT 로그아웃 핸들러 실행 완료");
    }
}