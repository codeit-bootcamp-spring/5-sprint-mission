package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.config.properties.JwtProperties;
import com.sprint.mission.discodeit.dto.jwt.data.JwtInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount;
    private final JwtTokenProvider tokenProvider;

    public InMemoryJwtRegistry(JwtTokenProvider tokenProvider, JwtProperties jwtProperties) {
        this.tokenProvider = tokenProvider;
        this.maxActiveJwtCount = jwtProperties.maxSessions();
        log.info("JWT Registry initialized with max concurrent sessions: {}", maxActiveJwtCount);
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();
        Queue<JwtInformation> queue = origin.computeIfAbsent(userId,
            k -> new ConcurrentLinkedQueue<>());

        while (queue.size() >= maxActiveJwtCount) {
            JwtInformation removed = queue.poll();
            if (removed != null) {
                accessTokenIndexes.remove(removed.accessToken());
                refreshTokenIndexes.remove(removed.refreshToken());
                log.debug("Removed old JWT for user {} due to max concurrent login limit",
                    userId);
            }
        }

        queue.offer(jwtInformation);
        accessTokenIndexes.add(jwtInformation.accessToken());
        refreshTokenIndexes.add(jwtInformation.refreshToken());
        log.debug("Registered JWT information for user: {}", userId);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> removed = origin.remove(userId);
        if (removed != null && !removed.isEmpty()) {
            for (JwtInformation info : removed) {
                accessTokenIndexes.remove(info.accessToken());
                refreshTokenIndexes.remove(info.refreshToken());
            }
            log.debug("Invalidated all JWT information for user: {}", userId);
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessTokenIndexes.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndexes.contains(refreshToken);
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        for (Queue<JwtInformation> queue : origin.values()) {
            boolean removed = queue.removeIf(info -> {
                if (refreshToken.equals(info.refreshToken())) {
                    accessTokenIndexes.remove(info.accessToken());
                    refreshTokenIndexes.remove(info.refreshToken());
                    return true;
                }
                return false;
            });

            if (removed) {
                queue.offer(newJwtInformation);
                accessTokenIndexes.add(newJwtInformation.accessToken());
                refreshTokenIndexes.add(newJwtInformation.refreshToken());
                log.debug("Rotated JWT information for user: {}",
                    newJwtInformation.userDto().id());
                return;
            }
        }
        log.warn("Failed to rotate JWT - refresh token not found in registry");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        int removedCount = 0;
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            Queue<JwtInformation> queue = entry.getValue();
            int beforeSize = queue.size();
            queue.removeIf(info -> {
                boolean expired = !tokenProvider.validateRefreshToken(info.refreshToken());
                if (expired) {
                    accessTokenIndexes.remove(info.accessToken());
                    refreshTokenIndexes.remove(info.refreshToken());
                }
                return expired;
            });
            removedCount += beforeSize - queue.size();

            if (queue.isEmpty()) {
                origin.remove(entry.getKey());
            }
        }
        if (removedCount > 0) {
            log.debug("Cleared {} expired JWT information entries", removedCount);
        }
    }
}
