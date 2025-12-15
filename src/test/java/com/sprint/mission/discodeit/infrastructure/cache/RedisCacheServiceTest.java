package com.sprint.mission.discodeit.infrastructure.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisCacheService 단위 테스트")
class RedisCacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private CacheProperties.Redis redisProperties;

    @InjectMocks
    private RedisCacheService cacheService;

    @Captor
    private ArgumentCaptor<Collection<String>> keysCaptor;

    private static final String CACHE_NAME = "testCache";
    private static final String KEY_PREFIX = "discodeit:";

    private void stubCacheProperties() {
        given(cacheProperties.getRedis()).willReturn(redisProperties);
        given(redisProperties.getKeyPrefix()).willReturn(KEY_PREFIX);
    }

    @Nested
    @DisplayName("evict")
    class EvictTest {

        @Mock
        private Cache cache;

        @Test
        @DisplayName("캐시가 존재하면 해당 키를 제거한다")
        void evict_withExistingCache_evictsKey() {
            // given
            String key = "testKey";
            given(cacheManager.getCache(CACHE_NAME)).willReturn(cache);

            // when
            cacheService.evict(CACHE_NAME, key);

            // then
            then(cache).should().evict(key);
        }

        @Test
        @DisplayName("캐시가 존재하지 않으면 아무 작업도 수행하지 않는다")
        void evict_withNonExistentCache_doesNothing() {
            // given
            given(cacheManager.getCache(CACHE_NAME)).willReturn(null);

            // when
            cacheService.evict(CACHE_NAME, "testKey");

            // then
            then(cacheManager).should().getCache(CACHE_NAME);
        }
    }

    @Nested
    @DisplayName("evictAll")
    class EvictAllTest {

        @Test
        @DisplayName("여러 키를 prefix와 함께 일괄 삭제한다")
        void evictAll_withValidKeys_deletesWithPrefix() {
            // given
            stubCacheProperties();
            List<String> keys = List.of("key1", "key2", "key3");
            given(redisTemplate.delete(anyCollection())).willReturn(3L);

            // when
            cacheService.evictAll(CACHE_NAME, keys);

            // then
            then(redisTemplate).should().delete(keysCaptor.capture());

            Collection<String> capturedKeys = keysCaptor.getValue();
            assertThat(capturedKeys).containsExactlyInAnyOrder(
                "discodeit:testCache::key1",
                "discodeit:testCache::key2",
                "discodeit:testCache::key3"
            );
        }

        @Test
        @DisplayName("키 컬렉션이 null이면 아무 작업도 수행하지 않는다")
        void evictAll_withNullKeys_doesNothing() {
            // when
            cacheService.evictAll(CACHE_NAME, null);

            // then
            then(redisTemplate).should(never()).delete(anyCollection());
        }

        @Test
        @DisplayName("키 컬렉션이 비어있으면 아무 작업도 수행하지 않는다")
        void evictAll_withEmptyKeys_doesNothing() {
            // when
            cacheService.evictAll(CACHE_NAME, Collections.emptyList());

            // then
            then(redisTemplate).should(never()).delete(anyCollection());
        }

        @Test
        @DisplayName("단일 키도 올바르게 처리한다")
        void evictAll_withSingleKey_deletesCorrectly() {
            // given
            stubCacheProperties();
            List<String> keys = List.of("singleKey");
            given(redisTemplate.delete(anyCollection())).willReturn(1L);

            // when
            cacheService.evictAll(CACHE_NAME, keys);

            // then
            then(redisTemplate).should().delete(keysCaptor.capture());

            Collection<String> capturedKeys = keysCaptor.getValue();
            assertThat(capturedKeys).containsExactly("discodeit:testCache::singleKey");
        }
    }

    @Nested
    @DisplayName("clear")
    class ClearTest {

        @Mock
        private Cache cache;

        @Test
        @DisplayName("캐시가 존재하면 모든 항목을 제거한다")
        void clear_withExistingCache_clearsAllEntries() {
            // given
            given(cacheManager.getCache(CACHE_NAME)).willReturn(cache);

            // when
            cacheService.clear(CACHE_NAME);

            // then
            then(cache).should().clear();
        }

        @Test
        @DisplayName("캐시가 존재하지 않으면 아무 작업도 수행하지 않는다")
        void clear_withNonExistentCache_doesNothing() {
            // given
            given(cacheManager.getCache(CACHE_NAME)).willReturn(null);

            // when
            cacheService.clear(CACHE_NAME);

            // then
            then(cacheManager).should().getCache(CACHE_NAME);
        }
    }

    @Nested
    @DisplayName("generateKey")
    class GenerateKeyTest {

        @Test
        @DisplayName("키 생성 시 prefix:cacheName::key 형식을 따른다")
        void evictAll_generatesCorrectKeyFormat() {
            // given
            stubCacheProperties();
            List<Object> keys = List.of(123, "stringKey");
            given(redisTemplate.delete(anyCollection())).willReturn(2L);

            // when
            cacheService.evictAll(CACHE_NAME, keys);

            // then
            then(redisTemplate).should().delete(keysCaptor.capture());

            Collection<String> capturedKeys = keysCaptor.getValue();
            assertThat(capturedKeys).containsExactlyInAnyOrder(
                "discodeit:testCache::123",
                "discodeit:testCache::stringKey"
            );
        }
    }
}
