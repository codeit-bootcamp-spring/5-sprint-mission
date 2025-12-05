package com.sprint.mission.discodeit.common.security.jwt.registry.impl;

import com.sprint.mission.discodeit.common.config.properties.JwtProperties;
import com.sprint.mission.discodeit.common.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.domain.auth.dto.JwtDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@ConditionalOnProperty(name = "discodeit.jwt.registry-type", havingValue = "in-memory", matchIfMissing = true)
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtDto>> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount;
    private final JwtTokenProvider tokenProvider;

    public InMemoryJwtRegistry(JwtTokenProvider tokenProvider, JwtProperties jwtProperties) {
        this.tokenProvider = tokenProvider;
        this.maxActiveJwtCount = jwtProperties.maxSessions();
        log.info("InMemoryJwtRegistry 초기화: maxSessions={}", maxActiveJwtCount);
    }

    @Override
    public void registerJwtInformation(JwtDto jwtDto) {
        UUID userId = jwtDto.userDetailsDto().id();
        Queue<JwtDto> queue = origin.computeIfAbsent(
            userId,
            key -> new ConcurrentLinkedQueue<>()
        );

        while (queue.size() >= maxActiveJwtCount) {
            JwtDto removed = queue.poll();
            if (removed != null) {
                accessTokenIndexes.remove(removed.accessToken());
                refreshTokenIndexes.remove(removed.refreshToken());
                log.debug("최대 동시 로그인 제한으로 {} 사용자의 이전 JWT 제거", userId);
            }
        }

        queue.offer(jwtDto);
        accessTokenIndexes.add(jwtDto.accessToken());
        refreshTokenIndexes.add(jwtDto.refreshToken());
        log.debug("등록된 JWT 정보: {}", jwtDto);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtDto> removed = origin.remove(userId);
        if (removed != null && !removed.isEmpty()) {
            for (JwtDto info : removed) {
                accessTokenIndexes.remove(info.accessToken());
                refreshTokenIndexes.remove(info.refreshToken());
            }
            log.debug("모든 JWT 정보가 무효화됨. 사용자: {}", userId);
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtDto> queue = origin.get(userId);
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
    public void rotateJwtInformation(String refreshToken, JwtDto newJwtDto) {
        for (Queue<JwtDto> queue : origin.values()) {
            boolean removed = queue.removeIf(info -> {
                if (refreshToken.equals(info.refreshToken())) {
                    accessTokenIndexes.remove(info.accessToken());
                    refreshTokenIndexes.remove(info.refreshToken());
                    return true;
                }
                return false;
            });

            if (removed) {
                queue.offer(newJwtDto);
                accessTokenIndexes.add(newJwtDto.accessToken());
                refreshTokenIndexes.add(newJwtDto.refreshToken());
                log.debug("Rotated JWT 정보. 사용자: {}", newJwtDto.userDetailsDto().id());
                return;
            }
        }
        log.warn("JWT rotation 실패 - refresh token이 registry에 없습니다.");
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        int removedCount = 0;
        for (Map.Entry<UUID, Queue<JwtDto>> entry : origin.entrySet()) {
            Queue<JwtDto> queue = entry.getValue();
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
            log.debug("{} 만료 JWT 정보 항목 정리", removedCount);
        }
    }
}
