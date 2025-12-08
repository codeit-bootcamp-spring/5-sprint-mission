package com.sprint.mission.discodeit.global.security.jwt.registry;

import com.sprint.mission.discodeit.global.security.jwt.dto.JwtDto;

import java.util.Set;
import java.util.UUID;

public interface JwtRegistry {

    void registerJwtInformation(JwtDto jwtDto);

    void invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtDto newJwtDto);

    void clearExpiredJwtInformation();

    Set<UUID> getActiveUserIds();
}
