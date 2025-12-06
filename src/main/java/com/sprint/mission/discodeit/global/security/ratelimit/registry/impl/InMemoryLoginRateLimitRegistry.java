package com.sprint.mission.discodeit.global.security.ratelimit.registry.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sprint.mission.discodeit.global.config.properties.RateLimitProperties;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@ConditionalOnProperty(name = "discodeit.rate-limit.registry-type", havingValue = "in-memory", matchIfMissing = true)
@Slf4j
public class InMemoryLoginRateLimitRegistry implements LoginRateLimitRegistry {

    private final Cache<String, Integer> attempts;
    private final Cache<String, Instant> blocked;
    private final int maxAttempts;
    private final Duration blockDuration;

    public InMemoryLoginRateLimitRegistry(RateLimitProperties properties) {
        this.maxAttempts = properties.maxAttempts();
        this.blockDuration = properties.blockDuration();

        this.attempts = Caffeine.newBuilder()
            .expireAfterWrite(properties.windowDuration())
            .build();

        this.blocked = Caffeine.newBuilder()
            .expireAfterWrite(properties.blockDuration())
            .build();

        log.info("InMemoryRateLimitRegistry initialized: maxAttempts={}, windowDuration={}, blockDuration={}",
            maxAttempts, properties.windowDuration(), blockDuration);
    }

    @Override
    public boolean isBlocked(String key) {
        Instant blockedUntil = blocked.getIfPresent(key);
        boolean isBlocked = blockedUntil != null && Instant.now().isBefore(blockedUntil);

        if (isBlocked) {
            log.debug("Key {} is blocked until {}", key, blockedUntil);
        }
        return isBlocked;
    }

    @Override
    public void recordAttempt(String key) {
        Integer currentCount = attempts.getIfPresent(key);
        int newCount = (currentCount == null) ? 1 : currentCount + 1;

        attempts.put(key, newCount);

        if (newCount >= maxAttempts) {
            Instant blockedUntil = Instant.now().plus(blockDuration);
            blocked.put(key, blockedUntil);
            attempts.invalidate(key);
            log.warn("Rate limit exceeded for key {}. Blocking until {}", key, blockedUntil);
        } else {
            log.debug("Recorded attempt for key {}: count={}", key, newCount);
        }
    }

    @Override
    public void resetAttempts(String key) {
        attempts.invalidate(key);
        blocked.invalidate(key);
        log.debug("Reset attempts for key {}", key);
    }

    @Override
    public int getRemainingAttempts(String key) {
        if (isBlocked(key)) {
            return 0;
        }

        Integer currentCount = attempts.getIfPresent(key);
        if (currentCount == null) {
            return maxAttempts;
        }

        return Math.max(0, maxAttempts - currentCount);
    }

    @Override
    public long getBlockedSecondsRemaining(String key) {
        Instant blockedUntil = blocked.getIfPresent(key);
        if (blockedUntil == null) {
            return 0;
        }

        Instant now = Instant.now();
        if (now.isAfter(blockedUntil)) {
            return 0;
        }

        return Duration.between(now, blockedUntil).toSeconds();
    }
}
