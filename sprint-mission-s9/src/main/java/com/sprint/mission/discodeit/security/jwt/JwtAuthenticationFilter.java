package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. 요청 헤더에서 JWT 토큰 추출
            String token = extractTokenFromRequest(request);

            // 2. 토큰이 있고 유효한 경우에만 인증 처리
            if (token != null && jwtTokenProvider.validateAccessToken(token)) {
                // 3. 토큰에서 사용자 정보 추출
                String username = jwtTokenProvider.getUsernameFromToken(token);
                UUID userId = jwtTokenProvider.getUserId(token);
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);

                // 4. Role enum 추출
                Role role = extractRoleFromAuthorities(roles);

                // 5. UserDto 생성
                UserDto userDto = new UserDto(userId, username, null, null, null, role);

                // 6. DiscodeitUserDetails 생성
                DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "");

                // 7. Authentication 객체 생성 및 SecurityContext에 저장
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                // 요청 정보 추가
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT authentication successful for user: {}", username);
            }

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            // 인증 실패 시 SecurityContext를 비워서 인증되지 않은 상태로 만듦
            SecurityContextHolder.clearContext();
        }

        // 6. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 Bearer 토큰 추출
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    // 권한 목록에서 Role enum 추출
    private Role extractRoleFromAuthorities(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Role.USER; // 기본값
        }

        String roleString = roles.get(0).replace("ROLE_", "");

        try {
            return Role.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown role: {}, defaulting to USER", roleString);
            return Role.USER;
        }
    }
}