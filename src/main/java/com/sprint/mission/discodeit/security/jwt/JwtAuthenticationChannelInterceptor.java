package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class
        );

        // CONNECT 프레임일 때만 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);

            if (StringUtils.hasText(token)) {
                try {
                    if (jwtTokenProvider.validateAccessToken(token) &&
                            jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {

                        String username = jwtTokenProvider.getUsernameFromToken(token);
                        DiscodeitUserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        accessor.setUser(authentication);

                        log.debug("WebSocket JWT 인증 성공: username={}", username);
                    } else {
                        log.warn("유효하지 않은 토큰 또는 Registry에 없는 토큰");
                        throw new BadCredentialsException("유효하지 않은 토큰입니다.");
                    }
                } catch (Exception e) {
                    log.error("WebSocket JWT 인증 실패: {}", e.getMessage());
                    throw new IllegalArgumentException("인증에 실패했습니다.", e);
                }
            } else {
                log.warn("WebSocket 연결 시 토큰이 제공되지 않음");
                throw new AuthenticationCredentialsNotFoundException("인증 토큰이 필요합니다.");
            }
        }

        return message;
    }

    /**
     * Authorization 헤더에서 토큰 추출
     * 형식: "Authorization: Bearer {token}"
     */
    private String resolveToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}