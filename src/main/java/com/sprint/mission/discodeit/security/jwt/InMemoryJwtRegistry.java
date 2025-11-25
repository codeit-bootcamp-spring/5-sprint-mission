package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
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
    private final JwtTokenProvider jwtTokenProvider;

    public InMemoryJwtRegistry(
            JwtProperties jwtProperties,
            JwtTokenProvider jwtTokenProvider) {
        this.maxActiveJwtCount = jwtProperties.getMaxActiveCount();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        origin.compute(jwtInformation.getUserResponse().getId(), (key, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }

            if (queue.size() >= maxActiveJwtCount) {
                JwtInformation oldestToken = queue.poll();
                if (oldestToken != null) {
                    removeTokenIndex(oldestToken.getAccessToken(), oldestToken.getRefreshToken());
                    log.debug("최대 동시 로그인 수 초과로 기존 토큰 제거: userId={}", key);
                }
            }

            queue.add(jwtInformation);
            addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());

            log.debug("JWT 정보 등록: userId={}, 활성 토큰 수={}", key, queue.size());
            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.computeIfPresent(userId, (key, queue) -> {
            queue.forEach(jwtInformation -> {
                removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
            });
            queue.clear();
            log.debug("사용자 토큰 무효화: userId={}", userId);
            return null;
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return origin.containsKey(userId);
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
        origin.computeIfPresent(newJwtInformation.getUserResponse().getId(), (key, queue) -> {
            queue.stream()
                    .filter(jwtInfo -> jwtInfo.getRefreshToken().equals(refreshToken))
                    .findFirst()
                    .ifPresent(jwtInfo -> {
                        removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
                        jwtInfo.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                        addTokenIndex(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                        log.debug("토큰 회전 완료: userId={}", key);
                    });
            return queue;
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry -> {
            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(jwtInfo -> {
                boolean isExpired =
                        !jwtTokenProvider.validateAccessToken(jwtInfo.getAccessToken()) ||
                                !jwtTokenProvider.validateRefreshToken(jwtInfo.getRefreshToken());

                if (isExpired) {
                    removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
                }
                return isExpired;
            });

            if (queue.isEmpty()) {
                log.debug("만료된 토큰 정리로 사용자 제거: userId={}", entry.getKey());
            }
            return queue.isEmpty();
        });
    }

    private void addTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.add(accessToken);
        refreshTokenIndexes.add(refreshToken);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.remove(accessToken);
        refreshTokenIndexes.remove(refreshToken);
    }
}