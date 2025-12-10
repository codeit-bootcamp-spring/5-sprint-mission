package com.sprint.mission.discodeit.security.jwt;


import com.sprint.mission.discodeit.dto.JwtDto.JwtInformation;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, JwtInformation> storage = new ConcurrentHashMap<>();

  @Override
  public void registerJwtInformation(UUID userId, JwtInformation jwtInformation) {
    storage.put(userId, jwtInformation);
  }

  @Override
  public Optional<JwtInformation> findByUserId(UUID userId) {
    return Optional.ofNullable(storage.get(userId));
  }

  @Override
  public Optional<JwtInformation> findByAccessToken(String accessToken) {

    return storage.values()
                  .stream()
                  .filter(info -> info.getAccessToken()
                                      .equals(accessToken))
                  .findFirst();
  }

  @Override
  public Optional<JwtInformation> findByRefreshToken(String refreshToken) {

    return storage.values()
                  .stream()
                  .filter(info -> info.getRefreshToken()
                                      .equals(refreshToken))
                  .findFirst();
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    storage.remove(userId);
  }

  @Override
  public void rotateJwtInformation(UUID userId, JwtInformation newJwtInformation) {
    storage.put(userId, newJwtInformation);
  }

  @Override
  public void clearExpiredJwtInformation() {

    Instant now = Instant.now();

    storage.entrySet()
           .removeIf(entry -> entry.getValue()
                                   .getRefreshTokenExpiresAt()
                                   .isBefore(now) && entry.getValue()
                                                          .getAccessTokenExpiresAt()
                                                          .isBefore(now));
  }
}