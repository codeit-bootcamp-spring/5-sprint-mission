package com.sprint.mission.discodeit.global.security.ratelimit.registry;

public interface LoginRateLimitRegistry {

    boolean isBlocked(String key);

    void recordAttempt(String key);

    void resetAttempts(String key);

    int getRemainingAttempts(String key);

    long getBlockedSecondsRemaining(String key);
}
