package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${security.jwt.max-active-count:1}")
    private int maxActiveJwtCount;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        
        origin.compute(userId, (key, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }
            
            while (queue.size() >= maxActiveJwtCount) {
                JwtInformation removed = queue.poll();
                if (removed != null) {
                    log.info("사용자 {}의 오래된 JWT 정보 제거됨 (동시 세션 수 초과)",
                            userId);
                }
            }
            
            queue.offer(jwtInformation);
            log.debug("사용자 {}의 JWT 정보 등록됨", userId);
            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> removed = origin.remove(userId);
        if (removed != null) {
            log.info("사용자 {}의 모든 JWT 정보 무효화됨", userId);
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);
        if (queue == null || queue.isEmpty()) {
            return false;
        }
        
        return queue.stream().anyMatch(this::isNotExpired);
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> 
                    info.getAccessToken().equals(accessToken) && 
                    isNotExpired(info) &&
                    jwtTokenProvider.validateAccessToken(accessToken)
                );
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> 
                    info.getRefreshToken().equals(refreshToken) && 
                    isNotExpired(info) &&
                    jwtTokenProvider.validateRefreshToken(refreshToken)
                );
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().id();
        
        origin.computeIfPresent(userId, (key, queue) -> {
            queue.stream()
                .filter(info -> info.getRefreshToken().equals(oldRefreshToken))
                .findFirst()
                .ifPresent(info -> info.rotate(
                    newJwtInformation.getAccessToken(), 
                    newJwtInformation.getRefreshToken()
                ));
            
            log.debug("사용자 {}의 JWT 정보가 갱신됨", userId);
            return queue;
        });
        
        if (!origin.containsKey(userId)) {
            registerJwtInformation(newJwtInformation);
        }
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5) // 5분마다 실행
    public void clearExpiredJwtInformation() {
        log.debug("만료된 JWT 정보 제거 시작");
        
        int removedCount = 0;
        
        Iterator<Map.Entry<UUID, Queue<JwtInformation>>> iterator = origin.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Queue<JwtInformation>> entry = iterator.next();
            Queue<JwtInformation> queue = entry.getValue();
            
            int beforeSize = queue.size();
            queue.removeIf(info -> !isNotExpired(info));
            removedCount += (beforeSize - queue.size());
            
            if (queue.isEmpty()) {
                iterator.remove();
            }
        }
        
        if (removedCount > 0) {
            log.info("만료된 JWT 정보 제거됨 : {}개", removedCount);
        }
    }

    private boolean isNotExpired(JwtInformation info) {
        return jwtTokenProvider.validateRefreshToken(info.getRefreshToken());
    }
}