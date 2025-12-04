package com.sprint.mission.discodeit.security.ratelimit;

public interface RateLimiterService {

    boolean isBlocked(String key);

    void recordAttempt(String key);

    void resetAttempts(String key);

    int getRemainingAttempts(String key);

    long getBlockedSecondsRemaining(String key);
}
