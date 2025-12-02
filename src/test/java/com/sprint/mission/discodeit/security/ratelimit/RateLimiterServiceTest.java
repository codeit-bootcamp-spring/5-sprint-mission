package com.sprint.mission.discodeit.security.ratelimit;

import com.sprint.mission.discodeit.config.properties.RateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RateLimiterService 단위 테스트")
class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties(3, 60, 300);
        rateLimiterService = new RateLimiterService(properties);
    }

    @Test
    @DisplayName("isBlocked - 새로운 키는 차단되지 않는다")
    void isBlocked_NewKey_ReturnsFalse() {
        // when
        boolean blocked = rateLimiterService.isBlocked("new-key");

        // then
        assertThat(blocked).isFalse();
    }

    @Test
    @DisplayName("isBlocked - 최대 시도 횟수 미만이면 차단되지 않는다")
    void isBlocked_BelowMaxAttempts_ReturnsFalse() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);

        // when
        boolean blocked = rateLimiterService.isBlocked(key);

        // then
        assertThat(blocked).isFalse();
    }

    @Test
    @DisplayName("isBlocked - 최대 시도 횟수 도달 시 차단된다")
    void isBlocked_AtMaxAttempts_ReturnsTrue() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);

        // when
        boolean blocked = rateLimiterService.isBlocked(key);

        // then
        assertThat(blocked).isTrue();
    }

    @Test
    @DisplayName("recordAttempt - 시도 횟수를 기록한다")
    void recordAttempt_RecordsAttempt() {
        // given
        String key = "test-key";

        // when
        rateLimiterService.recordAttempt(key);

        // then
        assertThat(rateLimiterService.getRemainingAttempts(key)).isEqualTo(2);
    }

    @Test
    @DisplayName("resetAttempts - 시도 횟수를 초기화한다")
    void resetAttempts_ClearsAttempts() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);

        // when
        rateLimiterService.resetAttempts(key);

        // then
        assertThat(rateLimiterService.getRemainingAttempts(key)).isEqualTo(3);
        assertThat(rateLimiterService.isBlocked(key)).isFalse();
    }

    @Test
    @DisplayName("getRemainingAttempts - 새로운 키는 최대 시도 횟수를 반환한다")
    void getRemainingAttempts_NewKey_ReturnsMaxAttempts() {
        // when
        int remaining = rateLimiterService.getRemainingAttempts("new-key");

        // then
        assertThat(remaining).isEqualTo(3);
    }

    @Test
    @DisplayName("getRemainingAttempts - 시도 후 남은 횟수를 반환한다")
    void getRemainingAttempts_AfterAttempts_ReturnsCorrectCount() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);

        // when
        int remaining = rateLimiterService.getRemainingAttempts(key);

        // then
        assertThat(remaining).isEqualTo(2);
    }

    @Test
    @DisplayName("getRemainingAttempts - 차단된 후에는 0을 반환한다")
    void getRemainingAttempts_WhenBlocked_ReturnsZero() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);

        // when
        int remaining = rateLimiterService.getRemainingAttempts(key);

        // then
        assertThat(remaining).isEqualTo(0);
    }

    @Test
    @DisplayName("getBlockedSecondsRemaining - 차단되지 않은 키는 0을 반환한다")
    void getBlockedSecondsRemaining_NotBlocked_ReturnsZero() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);

        // when
        long remaining = rateLimiterService.getBlockedSecondsRemaining(key);

        // then
        assertThat(remaining).isEqualTo(0);
    }

    @Test
    @DisplayName("getBlockedSecondsRemaining - 차단된 키는 남은 시간을 반환한다")
    void getBlockedSecondsRemaining_WhenBlocked_ReturnsPositiveValue() {
        // given
        String key = "test-key";
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);
        rateLimiterService.recordAttempt(key);

        // when
        long remaining = rateLimiterService.getBlockedSecondsRemaining(key);

        // then
        assertThat(remaining).isGreaterThan(0);
        assertThat(remaining).isLessThanOrEqualTo(300);
    }

    @Test
    @DisplayName("getBlockedSecondsRemaining - 존재하지 않는 키는 0을 반환한다")
    void getBlockedSecondsRemaining_UnknownKey_ReturnsZero() {
        // when
        long remaining = rateLimiterService.getBlockedSecondsRemaining("unknown-key");

        // then
        assertThat(remaining).isEqualTo(0);
    }

    @Test
    @DisplayName("cleanupExpiredEntries - 만료된 항목을 정리한다")
    void cleanupExpiredEntries_RemovesExpiredEntries() {
        // given
        RateLimitProperties shortWindowProperties = new RateLimitProperties(3, 1, 1);
        RateLimiterService shortWindowService = new RateLimiterService(shortWindowProperties);

        String key = "test-key";
        shortWindowService.recordAttempt(key);

        // when - wait for window to expire
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        shortWindowService.cleanupExpiredEntries();

        // then
        assertThat(shortWindowService.getRemainingAttempts(key)).isEqualTo(3);
    }

    @Test
    @DisplayName("여러 키에 대해 독립적으로 동작한다")
    void multipleKeys_WorkIndependently() {
        // given
        String key1 = "user1";
        String key2 = "user2";

        rateLimiterService.recordAttempt(key1);
        rateLimiterService.recordAttempt(key1);
        rateLimiterService.recordAttempt(key1);

        rateLimiterService.recordAttempt(key2);

        // then
        assertThat(rateLimiterService.isBlocked(key1)).isTrue();
        assertThat(rateLimiterService.isBlocked(key2)).isFalse();
        assertThat(rateLimiterService.getRemainingAttempts(key2)).isEqualTo(2);
    }
}
