package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.JwtDto.JwtInformation;
import java.util.Optional;
import java.util.UUID;

public interface JwtRegistry {


  void registerJwtInformation(UUID userId, JwtDto.JwtInformation jwtInformation);

  Optional<JwtInformation> findByUserId(UUID userId);

  Optional<JwtDto.JwtInformation> findByAccessToken(String accessToken);

  Optional<JwtDto.JwtInformation> findByRefreshToken(String refreshToken);

  void invalidateJwtInformationByUserId(UUID userId);

  void rotateJwtInformation(UUID userId, JwtDto.JwtInformation newJwtInformation);

  void clearExpiredJwtInformation();
}
