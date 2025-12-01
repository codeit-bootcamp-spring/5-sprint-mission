package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("캐시 통합 테스트")
class CacheIntegrationTest extends CacheClearTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private Cache usersCache;

    @BeforeEach
    void setUp() {
        usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.clear();
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
        assertThat(usersCache.get(SimpleKey.EMPTY)).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("findAll - 동일한 결과 반환 확인")
    void findAll_returnsSameResult() {
        // given
        userRepository.save(createUser("testuser2"));

        // when
        List<UserDto> firstCall = userService.findAll();
        List<UserDto> secondCall = userService.findAll();
        List<UserDto> thirdCall = userService.findAll();

        // then
        assertThat(firstCall).isEqualTo(secondCall);
        assertThat(secondCall).isEqualTo(thirdCall);
    }
}
