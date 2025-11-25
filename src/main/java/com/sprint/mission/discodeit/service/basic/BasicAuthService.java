package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.JwtDto.JwtInformation;
import com.sprint.mission.discodeit.dto.JwtDto.JwtResponse;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.auth.LoginFailedException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.config.JwtProperties;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserMapper userMapper;
  private final JwtProperties jwtProperties;

  @Override
  public JwtResponse refresh(String refreshToken, HttpServletResponse response) {

    JwtInformation jwtInfo = jwtRegistry.findByRefreshToken(refreshToken)
                                        .orElseThrow(LoginFailedException::new);

    String accessToken = jwtInfo.getAccessToken();
    UUID userId = jwtTokenProvider.getUserIdFromAccessToken(accessToken);
    String username = jwtTokenProvider.getUsernameFromAccessToken(accessToken);
    String role = jwtTokenProvider.getRoleFromAccessToken(accessToken);

    jwtRegistry.invalidateJwtInformationByUserId(userId);

    String newAccessToken = jwtTokenProvider.createAccessToken(userId, username, role);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, username, role);

    UserDto.DetailResponse userDetailResponse = userMapper.toDetailResponse(UserDto.Detail.builder()
                                                                                          .id(userId)
                                                                                          .username(
                                                                                              username)
                                                                                          .role(
                                                                                              role)
                                                                                          .build());

    JwtInformation newInfo = JwtInformation.builder()
                                           .accessToken(newAccessToken)
                                           .refreshToken(newRefreshToken)
                                           .accessTokenExpiresAt(Instant.now()
                                                                        .plusMillis(
                                                                            jwtProperties.getAccessTokenValidityMillis()))
                                           .refreshTokenExpiresAt(Instant.now()
                                                                         .plusMillis(
                                                                             jwtProperties.getRefreshTokenValidityMillis()))
                                           .build();

    jwtRegistry.rotateJwtInformation(userId, newInfo);

    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
                                                 .httpOnly(true)
                                                 .secure(true)
                                                 .path("/")
                                                 .maxAge(newInfo.getRefreshTokenExpiresAt()
                                                                .getEpochSecond())
                                                 .build();
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

    return JwtResponse.builder()
                      .userDto(userDetailResponse)
                      .accessToken(newAccessToken)
                      .build();
  }
}
