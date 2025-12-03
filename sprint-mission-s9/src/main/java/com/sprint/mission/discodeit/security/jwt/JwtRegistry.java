package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

public interface JwtRegistry {

    void registerJwtInformation(JwtInformation jwtInformation);
    void invalidateJwtInformationByUserId(UUID userId);
    boolean hasActiveJwtInformationByUserId(UUID userId);
    boolean hasActiveJwtInformationByAccessToken(String accessTokenId);
    boolean hasActiveJwtInformationByRefreshToken(String refreshTokenId);
    void rotateJwtInformation(String oldRefreshTokenId, JwtInformation newJwtInformation);
    void clearExpiredJwtInformation();
}