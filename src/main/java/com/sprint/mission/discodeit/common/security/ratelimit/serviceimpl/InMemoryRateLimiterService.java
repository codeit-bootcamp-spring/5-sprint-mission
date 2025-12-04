package com.sprint.mission.discodeit.common.security.ratelimit.serviceimpl;

import com.sprint.mission.discodeit.common.config.properties.RateLimitProperties;
import com.sprint.mission.discodeit.common.security.ratelimit.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnProperty(name = "discodeit.rate-limit.type", havingValue = "in-memory", matchIfMissing = true)
@Slf4j
public class InMemoryRateLimiterService implements RateLimiterService {

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final Duration windowDuration;
    private final Duration blockDuration;

    public InMemoryRateLimiterService(RateLimitProperties properties) {
        this.maxAttempts = properties.maxAttempts();
        this.windowDuration = properties.windowDuration();
        this.blockDuration = properties.blockDuration();
        log.info("InMemoryRateLimiterService initialized: maxAttempts={}, windowDuration={}, blockDuration={}",
            maxAttempts, windowDuration, blockDuration);
    }

    @Override
    public boolean isBlocked(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null) {
            return false;
        }

        Instant now = Instant.now();

        if (info.blockedUntil != null && now.isBefore(info.blockedUntil)) {
            log.debug("Key {} is blocked until {}", key, info.blockedUntil);
            return true;
        }

        if (info.blockedUntil != null && now.isAfter(info.blockedUntil)) {
            attempts.remove(key);
            return false;
        }

        return false;
    }

    @Override
    public void recordAttempt(String key) {
        Instant now = Instant.now();
        AttemptInfo info = attempts.compute(key, (k, existing) -> {
            if (existing == null || now.isAfter(existing.windowStart.plus(windowDuration))) {
                return new AttemptInfo(1, now, null);
            }

            int newCount = existing.count + 1;
            Instant blockedUntil = null;

            if (newCount >= maxAttempts) {
                blockedUntil = now.plus(blockDuration);
                log.warn("Rate limit exceeded for key {}, blocked until {}", key, blockedUntil);
            }

            return new AttemptInfo(newCount, existing.windowStart, blockedUntil);
        });

        log.debug("Recorded attempt for key {}: count={}", key, info.count);
    }

    @Override
    public void resetAttempts(String key) {
        attempts.remove(key);
        log.debug("Reset attempts for key {}", key);
    }

    @Override
    public int getRemainingAttempts(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null) {
            return maxAttempts;
        }

        Instant now = Instant.now();
        if (now.isAfter(info.windowStart.plus(windowDuration))) {
            return maxAttempts;
        }

        return Math.max(0, maxAttempts - info.count);
    }

    @Override
    public long getBlockedSecondsRemaining(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null || info.blockedUntil == null) {
            return 0;
        }

        Instant now = Instant.now();
        if (now.isAfter(info.blockedUntil)) {
            return 0;
        }

        return Duration.between(now, info.blockedUntil).toSeconds();
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredEntries() {
        Instant now = Instant.now();
        int beforeSize = attempts.size();

        attempts.entrySet().removeIf(entry -> {
            AttemptInfo info = entry.getValue();
            if (info.blockedUntil != null) {
                return now.isAfter(info.blockedUntil);
            }
            return now.isAfter(info.windowStart.plus(windowDuration));
        });

        int removed = beforeSize - attempts.size();
        if (removed > 0) {
            log.debug("Cleaned up {} expired rate limit entries", removed);
        }
    }

    private record AttemptInfo(int count, Instant windowStart, Instant blockedUntil) {
    }
}
