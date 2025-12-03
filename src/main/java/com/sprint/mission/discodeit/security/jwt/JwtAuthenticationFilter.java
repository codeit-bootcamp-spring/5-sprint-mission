package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.base.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      String token = resolveToken(request);

      if (StringUtils.hasText(token)) {
        if (tokenProvider.validateAccessToken(token) && jwtRegistry.hasActiveJwtInformationByAccessToken(
            token)) {
          String username = tokenProvider.getUsernameFromToken(token);

          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
              );

          authentication.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
          );

          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.debug("Set authentication for user: {}", username);
        } else {
          log.debug("Invalid JWT token");
          sendErrorResponse(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
      }
    } catch (Exception e) {
      log.debug("JWT authentication failed: {}", e.getMessage());
      SecurityContextHolder.clearContext();
      sendErrorResponse(response, "JWT authentication failed", HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private void sendErrorResponse(HttpServletResponse response, String message, int status)
          throws IOException {

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            resolveErrorCode(status),
            message,
            Map.of(),
            RuntimeException.class.getSimpleName(),
            status
    );

    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(jsonResponse);
  }

  private String resolveErrorCode(int status) {
    return switch (status) {
      case HttpServletResponse.SC_BAD_REQUEST -> "BAD_REQUEST";
      case HttpServletResponse.SC_UNAUTHORIZED -> "AUTHENTICATION_FAILED";
      case HttpServletResponse.SC_FORBIDDEN -> "ACCESS_DENIED";
      case HttpServletResponse.SC_NOT_FOUND -> "NOT_FOUND";
      case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> "INTERNAL_SERVER_ERROR";
      default -> "UNKNOWN_ERROR";
    };
  }
}