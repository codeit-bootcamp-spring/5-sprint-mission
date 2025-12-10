package com.sprint.mission.discodeit.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.JwtDto.JwtInformation;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.config.JwtProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserMapper userMapper;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final JwtProperties jwtProperties;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    UserDto.Detail userDetail = userDetails.getUserDetail();
    UUID userId = userDetail.getId();
    String username = userDetail.getUsername();
    String role = userDetail.getRole();

    jwtRegistry.invalidateJwtInformationByUserId(userId);

    String accessToken = jwtTokenProvider.createAccessToken(userId, username, role);
    String refreshToken = jwtTokenProvider.createRefreshToken(userId, username, role);

    JwtInformation info = JwtInformation.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .accessTokenExpiresAt(Instant.now()
                                                                     .plusMillis(
                                                                         jwtProperties.getAccessTokenValidityMillis()))
                                        .refreshTokenExpiresAt(Instant.now()
                                                                      .plusMillis(
                                                                          jwtProperties.getRefreshTokenValidityMillis()))
                                        .build();
    jwtRegistry.registerJwtInformation(userId, info);

    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge((int) (jwtProperties.getRefreshTokenValidityMillis() / 1000));
    response.addCookie(refreshCookie);

    UserDto.DetailResponse userDetailResponse = userMapper.toDetailResponse(userDetail);

    JwtDto.JwtResponse jwtResponse = JwtDto.JwtResponse.builder()
                                                       .accessToken(accessToken)
                                                       .userDto(userDetailResponse)
                                                       .build();

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter()
            .write(objectMapper.writeValueAsString(jwtResponse));
  }
}