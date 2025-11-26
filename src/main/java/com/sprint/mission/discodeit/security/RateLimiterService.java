package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.config.properties.RateLimitProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RateLimiterService {

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final int windowSeconds;
    private final int blockSeconds;

    public RateLimiterService(RateLimitProperties properties) {
        this.maxAttempts = properties.maxAttempts();
        this.windowSeconds = properties.windowSeconds();
        this.blockSeconds = properties.blockSeconds();
        log.info("RateLimiterService initialized: maxAttempts={}, windowSeconds={}, blockSeconds={}",
            maxAttempts, windowSeconds, blockSeconds);
    }

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

    public void recordAttempt(String key) {
        Instant now = Instant.now();
        AttemptInfo info = attempts.compute(key, (k, existing) -> {
            if (existing == null) {
                return new AttemptInfo(1, now, null);
            }

            if (now.isAfter(existing.windowStart.plusSeconds(windowSeconds))) {
                return new AttemptInfo(1, now, null);
            }

            int newCount = existing.count + 1;
            Instant blockedUntil = null;

            if (newCount >= maxAttempts) {
                blockedUntil = now.plusSeconds(blockSeconds);
                log.warn("Rate limit exceeded for key {}, blocked until {}", key, blockedUntil);
            }

            return new AttemptInfo(newCount, existing.windowStart, blockedUntil);
        });

        log.debug("Recorded attempt for key {}: count={}", key, info.count);
    }

    public void resetAttempts(String key) {
        attempts.remove(key);
        log.debug("Reset attempts for key {}", key);
    }

    public int getRemainingAttempts(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null) {
            return maxAttempts;
        }

        Instant now = Instant.now();
        if (now.isAfter(info.windowStart.plusSeconds(windowSeconds))) {
            return maxAttempts;
        }

        return Math.max(0, maxAttempts - info.count);
    }

    public long getBlockedSecondsRemaining(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null || info.blockedUntil == null) {
            return 0;
        }

        Instant now = Instant.now();
        if (now.isAfter(info.blockedUntil)) {
            return 0;
        }

        return info.blockedUntil.getEpochSecond() - now.getEpochSecond();
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
            return now.isAfter(info.windowStart.plusSeconds(windowSeconds));
        });

        int removed = beforeSize - attempts.size();
        if (removed > 0) {
            log.debug("Cleaned up {} expired rate limit entries", removed);
        }
    }

    private record AttemptInfo(int count, Instant windowStart, Instant blockedUntil) {
    }
}
