package com.sprint.mission.discodeit.common.security.jwt;

import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.domain.auth.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final JwtResponseWriter responseWriter;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(BEARER_PREFIX.length());
            if (!isValidToken(token)) {
                log.debug("유효하지 않은 JWT 토큰");
                responseWriter.writeError(response, new InvalidTokenException());
                return;
            }

            setAuthentication(token, request);
            log.debug("JWT 인증 설정 완료: username={}", tokenProvider.getUsernameFromToken(token));
        } catch (Exception e) {
            log.debug("JWT 인증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            responseWriter.writeError(response, new InvalidTokenException());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        return tokenProvider.validateAccessToken(token)
            && jwtRegistry.hasActiveJwtInformationByAccessToken(token);
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        String username = tokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
