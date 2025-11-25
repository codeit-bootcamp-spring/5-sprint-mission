package com.sprint.mission.discodeit.security.filter;

import com.sprint.mission.discodeit.dto.JwtDto.JwtInformation;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);

    if (!jwtTokenProvider.validateAccessToken(token)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    Optional<JwtInformation> optJwtInfo = jwtRegistry.findByAccessToken(token);
    if (optJwtInfo.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    UUID userId = jwtTokenProvider.getUserIdFromAccessToken(token);
    String username = jwtTokenProvider.getUsernameFromAccessToken(token);
    String role = jwtTokenProvider.getRoleFromAccessToken(token);

    JwtInformation jwtInfo = optJwtInfo.get();

    if (jwtInfo.getAccessTokenExpiresAt()
               .isBefore(Instant.now())) {
      jwtRegistry.invalidateJwtInformationByUserId(userId);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(UserDto.Detail.builder()
                                                                              .id(userId)
                                                                              .username(username)
                                                                              .role(role)
                                                                              .build(), null);

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext()
                         .setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}