package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 패킷의 header 영역을 검증(Authorization + Bearer)하여 토큰이 있는 경우 인증과 인가를 동시에 하는 필터
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtRegistry<Long> jwtRegistry;

    // 필터에서 제외할 request를 탐지할 메서드
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // /api/auth/refresh이 중요!!
        // 쿠키를 기반으로 refresh을 검증함으로 access 토큰이 무효할때도 인증하기 위해서
        return  path.equals("/api/auth/refresh") || path.equals("/api/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // header로 부터 토큰 추출
            String token = resolveToken(request);
            if(StringUtils.hasText(token)){
                // 토큰 자체가 유효한지 검증하고, +@ JWT 세션으로 부터 현재 유효한 토큰인지 검증하는 단계 추가!
                if(tokenProvider.validateAccessToken(token) &&
                        jwtRegistry.hasActiveJwtInformationByAccessToken(token)){
                    String username = tokenProvider.getUsernameFromToken(token);


                    // DB로 부터 다시 userDetails을 불러오는 과정인데, 권장하지 않는다.
                    // 리펙토링 권장!
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // token 기반으로 인증정보 살리는 방법, DB 조회하지 않음!!
                    UserDto userDto = tokenProvider.parseAccessToken(token).userDto();
                    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // authentication에 IP 주소나 sessionId 추가
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 인증 완료시 SecurityContextHolder에 인증정보 추가
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Set authentication for user: {}", username);
                } else {
                    // 토큰 자체가 유효하지 않거나, 세션으로 부터 허가되지 않은 사용자 일 때
                    log.debug("Invalid JWT token");
                    sendErrorResponse(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }else {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            log.error("Authentication failed. {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, "Authentication failed.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    // Authorization: Bearer xxx 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 공통 에러 응답
    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        //어떤 예외를 발생시켜야 하는지 헷갈려서 Runtime으로 대체
        ErrorResponse errorResponse = new ErrorResponse( new RuntimeException() , status);
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
