package com.sprint.mission.discodeit.integration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("캐시 통합 테스트")
class CacheIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private Cache<Object, Object> caffeineCache;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        org.springframework.cache.Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.clear();
            caffeineCache = (Cache<Object, Object>) cache.getNativeCache();
        }
    }

    @Test
    @Transactional
    @DisplayName("findAll - 첫 번째 호출은 DB 조회, 두 번째 호출은 캐시에서 반환")
    void findAll_cacheHitOnSecondCall() {
        // given
        userRepository.save(createUser("testuser"));

        // when - 첫 번째 호출 (캐시 미스, DB 조회)
        List<UserDto> firstCall = userService.findAll();
        int firstSize = firstCall.size();

        // when - 두 번째 호출 (캐시 히트)
        List<UserDto> secondCall = userService.findAll();

        // then - 동일한 결과 반환
        assertThat(secondCall).hasSize(firstSize);
        assertThat(firstCall).containsExactlyElementsOf(secondCall);

        // 캐시에 저장되어 있는지 확인
        assertThat(caffeineCache.getIfPresent(SimpleKey.EMPTY)).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("캐시 통계 확인 - hitCount 증가")
    void cacheStatistics() {
        // given
        userRepository.save(createUser("testuser2"));

        // 초기 통계 기록
        CacheStats initialStats = caffeineCache.stats();
        long initialHits = initialStats.hitCount();
        long initialMisses = initialStats.missCount();

        // when
        userService.findAll(); // miss
        userService.findAll(); // hit
        userService.findAll(); // hit

        // then - 상대적 증가량 확인
        CacheStats finalStats = caffeineCache.stats();

        assertThat(finalStats.missCount() - initialMisses).isEqualTo(1);
        assertThat(finalStats.hitCount() - initialHits).isEqualTo(2);
    }
}
