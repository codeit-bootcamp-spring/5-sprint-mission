package com.sprint.mission.discodeit.global.security.ratelimit.registry.impl;

import com.sprint.mission.discodeit.global.config.properties.RateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisLoginRateLimitRegistry 단위 테스트")
class RedisLoginRateLimitRegistryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisLoginRateLimitRegistry registry;

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW_DURATION = Duration.ofSeconds(60);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);
    private static final String TEST_KEY = "127.0.0.1";
    private static final String ATTEMPTS_KEY = "ratelimit:attempts:" + TEST_KEY;
    private static final String BLOCKED_KEY = "ratelimit:blocked:" + TEST_KEY;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties(
            MAX_ATTEMPTS, WINDOW_DURATION, BLOCK_DURATION, "redis"
        );
        registry = new RedisLoginRateLimitRegistry(redisTemplate, properties);
    }

    @Nested
    @DisplayName("isBlocked")
    class IsBlocked {

        @Test
        @DisplayName("차단된 키인 경우 true 반환")
        void isBlocked_blockedKey_returnsTrue() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(true);

            // when
            boolean result = registry.isBlocked(TEST_KEY);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("차단되지 않은 키인 경우 false 반환")
        void isBlocked_notBlockedKey_returnsFalse() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(false);

            // when
            boolean result = registry.isBlocked(TEST_KEY);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("recordAttempt")
    class RecordAttempt {

        @Test
        @DisplayName("첫 번째 시도 시 카운트 증가 및 만료 시간 설정")
        void recordAttempt_firstAttempt_incrementsAndSetsExpiry() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.increment(ATTEMPTS_KEY)).willReturn(1L);

            // when
            registry.recordAttempt(TEST_KEY);

            // then
            then(valueOperations).should().increment(ATTEMPTS_KEY);
            then(redisTemplate).should().expire(ATTEMPTS_KEY, WINDOW_DURATION);
        }

        @Test
        @DisplayName("연속 시도 시 카운트만 증가 (만료 시간 재설정 안함)")
        void recordAttempt_subsequentAttempt_onlyIncrements() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.increment(ATTEMPTS_KEY)).willReturn(3L);

            // when
            registry.recordAttempt(TEST_KEY);

            // then
            then(valueOperations).should().increment(ATTEMPTS_KEY);
            then(redisTemplate).should(never()).expire(ATTEMPTS_KEY, WINDOW_DURATION);
        }

        @Test
        @DisplayName("최대 시도 횟수 도달 시 차단 키 설정 및 시도 횟수 삭제")
        void recordAttempt_maxAttemptsReached_blocksAndResetsAttempts() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.increment(ATTEMPTS_KEY)).willReturn((long) MAX_ATTEMPTS);

            // when
            registry.recordAttempt(TEST_KEY);

            // then
            then(valueOperations).should().set(BLOCKED_KEY, "blocked", BLOCK_DURATION);
            then(redisTemplate).should().delete(ATTEMPTS_KEY);
        }

        @Test
        @DisplayName("최대 시도 횟수 초과 시에도 차단 처리")
        void recordAttempt_exceedsMaxAttempts_blocksAndResetsAttempts() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.increment(ATTEMPTS_KEY)).willReturn((long) MAX_ATTEMPTS + 1);

            // when
            registry.recordAttempt(TEST_KEY);

            // then
            then(valueOperations).should().set(BLOCKED_KEY, "blocked", BLOCK_DURATION);
            then(redisTemplate).should().delete(ATTEMPTS_KEY);
        }

        @Test
        @DisplayName("increment 결과가 null인 경우 차단하지 않음")
        void recordAttempt_nullCount_doesNotBlock() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.increment(ATTEMPTS_KEY)).willReturn(null);

            // when
            registry.recordAttempt(TEST_KEY);

            // then
            then(valueOperations).should(never()).set(BLOCKED_KEY, "blocked", BLOCK_DURATION);
            then(redisTemplate).should(never()).delete(ATTEMPTS_KEY);
        }
    }

    @Nested
    @DisplayName("resetAttempts")
    class ResetAttempts {

        @Test
        @DisplayName("시도 횟수와 차단 상태 모두 삭제")
        void resetAttempts_deletesBothKeys() {
            // when
            registry.resetAttempts(TEST_KEY);

            // then
            then(redisTemplate).should().delete(ATTEMPTS_KEY);
            then(redisTemplate).should().delete(BLOCKED_KEY);
        }
    }

    @Nested
    @DisplayName("getRemainingAttempts")
    class GetRemainingAttempts {

        @Test
        @DisplayName("차단된 경우 0 반환")
        void getRemainingAttempts_blocked_returnsZero() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(true);

            // when
            int result = registry.getRemainingAttempts(TEST_KEY);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("시도 기록이 없는 경우 최대 시도 횟수 반환")
        void getRemainingAttempts_noAttempts_returnsMaxAttempts() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(false);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(ATTEMPTS_KEY)).willReturn(null);

            // when
            int result = registry.getRemainingAttempts(TEST_KEY);

            // then
            assertThat(result).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("일부 시도 후 남은 횟수 반환")
        void getRemainingAttempts_someAttempts_returnsRemainingCount() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(false);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(ATTEMPTS_KEY)).willReturn("3");

            // when
            int result = registry.getRemainingAttempts(TEST_KEY);

            // then
            assertThat(result).isEqualTo(MAX_ATTEMPTS - 3);
        }

        @Test
        @DisplayName("시도 횟수가 최대에 도달한 경우 0 반환")
        void getRemainingAttempts_maxAttemptsReached_returnsZero() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(false);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(ATTEMPTS_KEY)).willReturn(String.valueOf(MAX_ATTEMPTS));

            // when
            int result = registry.getRemainingAttempts(TEST_KEY);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("잘못된 형식의 값이 저장된 경우 최대 시도 횟수 반환")
        void getRemainingAttempts_invalidValue_returnsMaxAttempts() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(false);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(ATTEMPTS_KEY)).willReturn("invalid");

            // when
            int result = registry.getRemainingAttempts(TEST_KEY);

            // then
            assertThat(result).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("시도 횟수가 최대를 초과한 경우 0 반환 (음수 방지)")
        void getRemainingAttempts_exceedsMax_returnsZero() {
            // given
            given(redisTemplate.hasKey(BLOCKED_KEY)).willReturn(false);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(ATTEMPTS_KEY)).willReturn(String.valueOf(MAX_ATTEMPTS + 5));

            // when
            int result = registry.getRemainingAttempts(TEST_KEY);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("getBlockedSecondsRemaining")
    class GetBlockedSecondsRemaining {

        @Test
        @DisplayName("차단된 경우 남은 시간 반환")
        void getBlockedSecondsRemaining_blocked_returnsRemainingSeconds() {
            // given
            long remainingSeconds = 120L;
            given(redisTemplate.getExpire(BLOCKED_KEY, TimeUnit.SECONDS))
                .willReturn(remainingSeconds);

            // when
            long result = registry.getBlockedSecondsRemaining(TEST_KEY);

            // then
            assertThat(result).isEqualTo(remainingSeconds);
        }

        @Test
        @DisplayName("차단되지 않은 경우 0 반환")
        void getBlockedSecondsRemaining_notBlocked_returnsZero() {
            // given
            given(redisTemplate.getExpire(BLOCKED_KEY, TimeUnit.SECONDS)).willReturn(-2L);

            // when
            long result = registry.getBlockedSecondsRemaining(TEST_KEY);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("TTL이 만료된 경우 0 반환")
        void getBlockedSecondsRemaining_expired_returnsZero() {
            // given
            given(redisTemplate.getExpire(BLOCKED_KEY, TimeUnit.SECONDS)).willReturn(-1L);

            // when
            long result = registry.getBlockedSecondsRemaining(TEST_KEY);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("TTL이 0인 경우 0 반환")
        void getBlockedSecondsRemaining_zeroTtl_returnsZero() {
            // given
            given(redisTemplate.getExpire(BLOCKED_KEY, TimeUnit.SECONDS)).willReturn(0L);

            // when
            long result = registry.getBlockedSecondsRemaining(TEST_KEY);

            // then
            assertThat(result).isZero();
        }
    }
}
