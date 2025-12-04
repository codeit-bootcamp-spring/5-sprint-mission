package com.sprint.mission.discodeit.security.ratelimit.serviceimpl;

import com.sprint.mission.discodeit.config.properties.RateLimitProperties;
import com.sprint.mission.discodeit.security.ratelimit.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "discodeit.rate-limit.type", havingValue = "redis")
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimiterService implements RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitProperties properties;

    // Key Prefix 상수 정의
    private static final String ATTEMPTS_KEY_PREFIX = "ratelimit:attempts:";
    private static final String BLOCKED_KEY_PREFIX = "ratelimit:blocked:";

    @Override
    public boolean isBlocked(String key) {
        String blockedKey = BLOCKED_KEY_PREFIX + key;
        Boolean hasKey = redisTemplate.hasKey(blockedKey);

        if (hasKey) {
            log.debug("Key {} is currently blocked.", key);
            return true;
        }
        return false;
    }

    @Override
    public void recordAttempt(String key) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + key;
        String blockedKey = BLOCKED_KEY_PREFIX + key;

        // 이미 차단된 상태라면 카운트 증가 로직을 수행하지 않음 (선택 사항)
        if (isBlocked(key)) {
            return;
        }

        // 1. "ratelimit:attempts:{key}" 키에 대해 INCR 실행
        // increment는 키가 없으면 0으로 생성 후 1을 증가시킴 (Atomic Operation)
        Long count = redisTemplate.opsForValue().increment(attemptsKey);

        // 2. 만약 결과가 1이면(최초 시도), 키에 EXPIRE를 window 초로 설정
        if (count != null && count == 1) {
            redisTemplate.expire(attemptsKey, properties.windowDuration());
        }

        // 3. 결과가 maxAttempts 이상이면, "ratelimit:blocked:{key}" 키를 생성하고 EXPIRE를 block 초로 설정
        if (count != null && count >= properties.maxAttempts()) {
            log.warn("Rate limit exceeded for key {}. Blocking for {}", key, properties.blockDuration());

            // 차단 키 설정 (값은 의미 없으므로 "blocked" 등으로 설정)
            redisTemplate.opsForValue().set(blockedKey, "blocked", properties.blockDuration());

            // 차단되었으므로 기존 카운트 키는 삭제하여, 차단 해제 후 0부터 다시 시작하게 함
            // (InMemory 로직과 동일하게 맞춤)
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

        // "ratelimit:attempts:{key}" 키를 삭제 (DEL)
        // 차단된 상태도 해제하는 것이 일반적인 'Reset'의 의미이므로 blockedKey도 같이 삭제
        redisTemplate.delete(attemptsKey);
        redisTemplate.delete(blockedKey);
    }

    @Override
    public int getRemainingAttempts(String key) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + key;

        // 차단된 상태면 남은 횟수는 0
        if (isBlocked(key)) {
            return 0;
        }

        // 1. "ratelimit:attempts:{key}" 키의 값을 가져옴 (GET)
        String countStr = redisTemplate.opsForValue().get(attemptsKey);

        int currentCount = 0;
        if (countStr != null) {
            try {
                currentCount = Integer.parseInt(countStr);
            } catch (NumberFormatException e) {
                log.error("Invalid attempt count value for key {}: {}", key, countStr);
            }
        }

        // 2. maxAttempts에서 현재 값을 빼서 반환
        return Math.max(0, properties.maxAttempts() - currentCount);
    }

    @Override
    public long getBlockedSecondsRemaining(String key) {
        String blockedKey = BLOCKED_KEY_PREFIX + key;

        // "ratelimit:blocked:{key}" 키의 남은 TTL을 반환 (TTL)
        // getExpire는 키가 없으면 -2, 만료시간이 없으면 -1을 반환함
        long ttl = redisTemplate.getExpire(blockedKey, TimeUnit.SECONDS);

        if (ttl > 0) {
            return ttl;
        }
        return 0;
    }
}
