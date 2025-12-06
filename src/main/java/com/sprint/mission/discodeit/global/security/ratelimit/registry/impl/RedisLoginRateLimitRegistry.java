package com.sprint.mission.discodeit.global.security.ratelimit.registry.impl;

import com.sprint.mission.discodeit.global.config.properties.RateLimitProperties;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "discodeit.rate-limit.registry-type", havingValue = "redis")
@RequiredArgsConstructor
@Slf4j
public class RedisLoginRateLimitRegistry implements LoginRateLimitRegistry {

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitProperties properties;

    private static final String ATTEMPTS_KEY_PREFIX = "ratelimit:attempts:";
    private static final String BLOCKED_KEY_PREFIX = "ratelimit:blocked:";

    @Override
    public boolean isBlocked(String key) {
        String blockedKey = BLOCKED_KEY_PREFIX + key;
        boolean blocked = redisTemplate.hasKey(blockedKey);

        if (blocked) {
            log.debug("Key {} is currently blocked.", key);
        }
        return blocked;
    }

    @Override
    public void recordAttempt(String key) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + key;
        String blockedKey = BLOCKED_KEY_PREFIX + key;

        Long count = redisTemplate.opsForValue().increment(attemptsKey);

        if (count != null && count == 1) {
            redisTemplate.expire(attemptsKey, properties.windowDuration());
        }

        if (count != null && count >= properties.maxAttempts()) {
            log.warn("Rate limit exceeded for key {}. Blocking for {}", key, properties.blockDuration());

            redisTemplate.opsForValue().set(blockedKey, "blocked", properties.blockDuration());

            redisTemplate.delete(attemptsKey);
        } else {
            log.debug("Recorded attempt for key {}: count={}", key, count);
        }
    }

    @Override
    public void resetAttempts(String key) {
        log.debug("Resetting attempts and block status for key {}", key);
        String attemptsKey = ATTEMPTS_KEY_PREFIX + key;
        String blockedKey = BLOCKED_KEY_PREFIX + key;

        redisTemplate.delete(attemptsKey);
        redisTemplate.delete(blockedKey);
    }

    @Override
    public int getRemainingAttempts(String key) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + key;

        if (isBlocked(key)) {
            return 0;
        }

        String countStr = redisTemplate.opsForValue().get(attemptsKey);

        int currentCount = 0;
        if (countStr != null) {
            try {
                currentCount = Integer.parseInt(countStr);
            } catch (NumberFormatException e) {
                log.error("Invalid attempt count value for key {}: {}", key, countStr);
            }
        }

        return Math.max(0, properties.maxAttempts() - currentCount);
    }

    @Override
    public long getBlockedSecondsRemaining(String key) {
        String blockedKey = BLOCKED_KEY_PREFIX + key;

        long ttl = redisTemplate.getExpire(blockedKey, TimeUnit.SECONDS);

        if (ttl > 0) {
            return ttl;
        }
        return 0;
    }
}
