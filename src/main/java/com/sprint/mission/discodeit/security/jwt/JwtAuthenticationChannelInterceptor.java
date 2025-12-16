package com.sprint.mission.discodeit.security.jwt;


import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider tokenProvider;
  private final RoleHierarchy roleHierarchy;
  private final JwtRegistry<UUID> jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
        message, StompHeaderAccessor.class
    );

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

      String token = resolveToken(accessor)
          .orElseThrow(() -> new RuntimeException("INVALID_TOKEN"));

      // HTTP 필터와 동일한 로직: 토큰 검증 + JWT 세션 확인
      if (tokenProvider.validateAccessToken(token)
          && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {

        // DB 조회 없이 토큰에서 바로 UserDto 복원
        UserDTO userDTO = tokenProvider.parseAccessToken(token).userDTO();

        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDTO, null);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                roleHierarchy.getReachableGrantedAuthorities(
                    userDetails.getAuthorities()
                )
            );

        accessor.setUser(authentication);
        log.debug("Set authentication for websocket user: {}", userDTO.username());
      } else {
        log.debug("Invalid JWT token for websocket connect");
        throw new RuntimeException("INVALID_TOKEN");
      }
    }

    return message;
  }

  /**
   * STOMP CONNECT 헤더에서 JWT 추출 1) Authorization: Bearer xxx 2) ACCESS_TOKEN: xxx (Bearer 없이 순수 토큰)
   */
  private Optional<String> resolveToken(StompHeaderAccessor accessor) {
    String prefix = "Bearer ";

    // 1) Authorization 헤더 우선
    String authHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith(prefix)) {
      return Optional.of(authHeader.substring(prefix.length()));
    }

    // 2) 커스텀 헤더 ACCESS_TOKEN 지원
    String accessTokenHeader = accessor.getFirstNativeHeader("REFRESH_TOKEN");
    if (StringUtils.hasText(accessTokenHeader)) {
      return Optional.of(accessTokenHeader);
    }

    // 3) 둘 다 없으면 빈 Optional
    return Optional.empty();
  }
}
