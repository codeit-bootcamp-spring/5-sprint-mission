package com.sprint.mission.discodeit.global.security.ratelimit.registry.impl;

import com.sprint.mission.discodeit.global.config.properties.RateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InMemoryLoginRateLimitRegistry 단위 테스트")
class InMemoryLoginRateLimitRegistryTest {

    private InMemoryLoginRateLimitRegistry registry;

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration WINDOW_DURATION = Duration.ofSeconds(60);
    private static final Duration BLOCK_DURATION = Duration.ofSeconds(300);
    private static final String TEST_KEY = "testuser@127.0.0.1";

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties(
            MAX_ATTEMPTS,
            WINDOW_DURATION,
            BLOCK_DURATION,
            "in-memory"
        );
        registry = new InMemoryLoginRateLimitRegistry(properties);
    }

    @Nested
    @DisplayName("isBlocked")
    class IsBlocked {

        @Test
        @DisplayName("시도 기록이 없는 경우 차단되지 않음")
        void isBlocked_noAttempts_returnsFalse() {
            // when & then
            assertThat(registry.isBlocked(TEST_KEY)).isFalse();
        }

        @Test
        @DisplayName("최대 시도 횟수 미만인 경우 차단되지 않음")
        void isBlocked_belowMaxAttempts_returnsFalse() {
            // given
            registry.recordAttempt(TEST_KEY);
            registry.recordAttempt(TEST_KEY);

            // when & then
            assertThat(registry.isBlocked(TEST_KEY)).isFalse();
        }

        @Test
        @DisplayName("최대 시도 횟수 도달 시 차단됨")
        void isBlocked_atMaxAttempts_returnsTrue() {
            // given
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(TEST_KEY);
            }

            // when & then
            assertThat(registry.isBlocked(TEST_KEY)).isTrue();
        }

        @Test
        @DisplayName("다른 키는 영향받지 않음")
        void isBlocked_differentKey_independent() {
            // given
            String anotherKey = "anotheruser@192.168.1.1";
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(TEST_KEY);
            }

            // when & then
            assertThat(registry.isBlocked(TEST_KEY)).isTrue();
            assertThat(registry.isBlocked(anotherKey)).isFalse();
        }
    }

    @Nested
    @DisplayName("recordAttempt")
    class RecordAttempt {

        @Test
        @DisplayName("첫 번째 시도 기록")
        void recordAttempt_firstAttempt_recorded() {
            // when
            registry.recordAttempt(TEST_KEY);

            // then
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isEqualTo(MAX_ATTEMPTS - 1);
        }

        @Test
        @DisplayName("여러 시도 기록 시 남은 시도 횟수 감소")
        void recordAttempt_multipleAttempts_decreasesRemainingAttempts() {
            // when
            registry.recordAttempt(TEST_KEY);
            registry.recordAttempt(TEST_KEY);

            // then
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isEqualTo(MAX_ATTEMPTS - 2);
        }

        @Test
        @DisplayName("최대 시도 횟수 도달 시 차단 및 시도 횟수 초기화")
        void recordAttempt_reachesMaxAttempts_blocksAndResetsAttempts() {
            // when
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(TEST_KEY);
            }

            // then
            assertThat(registry.isBlocked(TEST_KEY)).isTrue();
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isZero();
        }
    }

    @Nested
    @DisplayName("resetAttempts")
    class ResetAttempts {

        @Test
        @DisplayName("시도 횟수 초기화")
        void resetAttempts_clearsAttempts() {
            // given
            registry.recordAttempt(TEST_KEY);
            registry.recordAttempt(TEST_KEY);

            // when
            registry.resetAttempts(TEST_KEY);

            // then
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("차단 상태도 함께 초기화")
        void resetAttempts_clearsBlockStatus() {
            // given
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(TEST_KEY);
            }
            assertThat(registry.isBlocked(TEST_KEY)).isTrue();

            // when
            registry.resetAttempts(TEST_KEY);

            // then
            assertThat(registry.isBlocked(TEST_KEY)).isFalse();
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("기록이 없는 키 초기화 시 아무 동작 없음")
        void resetAttempts_nonExistingKey_noOp() {
            // when & then (예외 없음)
            registry.resetAttempts("non-existing-key");
            assertThat(registry.getRemainingAttempts("non-existing-key")).isEqualTo(MAX_ATTEMPTS);
        }
    }

    @Nested
    @DisplayName("getRemainingAttempts")
    class GetRemainingAttempts {

        @Test
        @DisplayName("시도 기록이 없는 경우 최대 시도 횟수 반환")
        void getRemainingAttempts_noAttempts_returnsMaxAttempts() {
            // when & then
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("시도 기록 후 남은 횟수 반환")
        void getRemainingAttempts_afterAttempts_returnsCorrectCount() {
            // given
            registry.recordAttempt(TEST_KEY);

            // when & then
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isEqualTo(MAX_ATTEMPTS - 1);
        }

        @Test
        @DisplayName("차단된 경우 0 반환")
        void getRemainingAttempts_blocked_returnsZero() {
            // given
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(TEST_KEY);
            }

            // when & then
            assertThat(registry.getRemainingAttempts(TEST_KEY)).isZero();
        }
    }

    @Nested
    @DisplayName("getBlockedSecondsRemaining")
    class GetBlockedSecondsRemaining {

        @Test
        @DisplayName("차단되지 않은 경우 0 반환")
        void getBlockedSecondsRemaining_notBlocked_returnsZero() {
            // when & then
            assertThat(registry.getBlockedSecondsRemaining(TEST_KEY)).isZero();
        }

        @Test
        @DisplayName("차단된 경우 남은 시간 반환")
        void getBlockedSecondsRemaining_blocked_returnsRemainingSeconds() {
            // given
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(TEST_KEY);
            }

            // when
            long remainingSeconds = registry.getBlockedSecondsRemaining(TEST_KEY);

            // then
            assertThat(remainingSeconds).isPositive();
            assertThat(remainingSeconds).isLessThanOrEqualTo(BLOCK_DURATION.toSeconds());
        }

        @Test
        @DisplayName("기록이 없는 키는 0 반환")
        void getBlockedSecondsRemaining_nonExistingKey_returnsZero() {
            // when & then
            assertThat(registry.getBlockedSecondsRemaining("non-existing-key")).isZero();
        }
    }

    @Nested
    @DisplayName("경계 조건")
    class EdgeCases {

        @Test
        @DisplayName("maxAttempts가 1인 경우 첫 시도에서 차단")
        void singleMaxAttempt_blocksOnFirstAttempt() {
            // given
            RateLimitProperties singleAttemptProps = new RateLimitProperties(
                1,
                WINDOW_DURATION,
                BLOCK_DURATION,
                "in-memory"
            );
            InMemoryLoginRateLimitRegistry singleAttemptRegistry =
                new InMemoryLoginRateLimitRegistry(singleAttemptProps);

            // when
            singleAttemptRegistry.recordAttempt(TEST_KEY);

            // then
            assertThat(singleAttemptRegistry.isBlocked(TEST_KEY)).isTrue();
            assertThat(singleAttemptRegistry.getRemainingAttempts(TEST_KEY)).isZero();
        }

        @Test
        @DisplayName("여러 다른 키에 대한 독립적인 처리")
        void multipleKeys_independentTracking() {
            // given
            String key1 = "user1@127.0.0.1";
            String key2 = "user2@127.0.0.1";
            String key3 = "user1@192.168.1.1";

            // when
            registry.recordAttempt(key1);
            registry.recordAttempt(key1);
            registry.recordAttempt(key2);
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                registry.recordAttempt(key3);
            }

            // then
            assertThat(registry.getRemainingAttempts(key1)).isEqualTo(MAX_ATTEMPTS - 2);
            assertThat(registry.getRemainingAttempts(key2)).isEqualTo(MAX_ATTEMPTS - 1);
            assertThat(registry.isBlocked(key3)).isTrue();
            assertThat(registry.getRemainingAttempts(key3)).isZero();
        }
    }
}
