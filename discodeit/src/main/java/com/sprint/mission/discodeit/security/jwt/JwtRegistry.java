package com.sprint.mission.discodeit.security.jwt;


import com.sprint.mission.discodeit.dto.data.JwtInformation;

// JWT 기반 세션 관리 인터페이스
public interface JwtRegistry<T> {

  void registerJwtInformation(JwtInformation jwtInformation);

  void invalidateJwtInformationByUserId(T userId);

  boolean hasActiveJwtInformationByUserId(T userId);

  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

  void clearExpiredJwtInformation();
}
