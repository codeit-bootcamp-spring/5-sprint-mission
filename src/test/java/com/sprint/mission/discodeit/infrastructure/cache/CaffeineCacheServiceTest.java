package com.sprint.mission.discodeit.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("CaffeineCacheService 단위 테스트")
class CaffeineCacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CaffeineCacheService cacheService;

    private static final String CACHE_NAME = "testCache";

    @Nested
    @DisplayName("evict")
    class EvictTest {

        @Mock
        private org.springframework.cache.Cache springCache;

        @Test
        @DisplayName("캐시와 키가 유효하면 해당 키를 제거한다")
        void evict_withValidCacheAndKey_evictsKey() {
            // given
            String key = "testKey";
            given(cacheManager.getCache(CACHE_NAME)).willReturn(springCache);

            // when
            cacheService.evict(CACHE_NAME, key);

            // then
            then(springCache).should().evict(key);
        }

        @Test
        @DisplayName("키가 null이면 아무 작업도 수행하지 않는다")
        void evict_withNullKey_doesNothing() {
            // when
            cacheService.evict(CACHE_NAME, null);

            // then
            then(cacheManager).should(never()).getCache(CACHE_NAME);
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        @DisplayName("캐시 이름이 null이면 아무 작업도 수행하지 않는다")
        void evict_withNullCacheName_doesNothing() {
            // when
            cacheService.evict(null, "testKey");

            // then
            then(cacheManager).should(never()).getCache(null);
        }

        @Test
        @DisplayName("캐시 이름이 빈 문자열이면 아무 작업도 수행하지 않는다")
        void evict_withEmptyCacheName_doesNothing() {
            // when
            cacheService.evict("", "testKey");

            // then
            then(cacheManager).should(never()).getCache("");
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

        @Mock
        private org.springframework.cache.Cache springCache;

        @Mock
        private Cache<Object, Object> caffeineNativeCache;

        @Test
        @DisplayName("Caffeine 캐시에서 여러 키를 일괄 제거한다")
        void evictAll_withCaffeineCache_invalidatesAllKeys() {
            // given
            List<String> keys = List.of("key1", "key2", "key3");
            given(cacheManager.getCache(CACHE_NAME)).willReturn(springCache);
            given(springCache.getNativeCache()).willReturn(caffeineNativeCache);

            // when
            cacheService.evictAll(CACHE_NAME, keys);

            // then
            then(caffeineNativeCache).should().invalidateAll(keys);
        }

        @Test
        @DisplayName("네이티브 캐시가 Caffeine이 아니면 개별 evict로 폴백한다")
        void evictAll_withNonCaffeineCache_fallsBackToIndividualEvict() {
            // given
            List<String> keys = List.of("key1", "key2");
            Object nonCaffeineNativeCache = new Object();
            given(cacheManager.getCache(CACHE_NAME)).willReturn(springCache);
            given(springCache.getNativeCache()).willReturn(nonCaffeineNativeCache);

            // when
            cacheService.evictAll(CACHE_NAME, keys);

            // then
            then(springCache).should().evict("key1");
            then(springCache).should().evict("key2");
        }

        @Test
        @DisplayName("키 컬렉션이 null이면 아무 작업도 수행하지 않는다")
        void evictAll_withNullKeys_doesNothing() {
            // when
            cacheService.evictAll(CACHE_NAME, null);

            // then
            then(cacheManager).should(never()).getCache(CACHE_NAME);
        }

        @Test
        @DisplayName("키 컬렉션이 비어있으면 아무 작업도 수행하지 않는다")
        void evictAll_withEmptyKeys_doesNothing() {
            // when
            cacheService.evictAll(CACHE_NAME, Collections.emptyList());

            // then
            then(cacheManager).should(never()).getCache(CACHE_NAME);
        }

        @Test
        @DisplayName("캐시가 존재하지 않으면 아무 작업도 수행하지 않는다")
        void evictAll_withNonExistentCache_doesNothing() {
            // given
            List<String> keys = List.of("key1");
            given(cacheManager.getCache(CACHE_NAME)).willReturn(null);

            // when
            cacheService.evictAll(CACHE_NAME, keys);

            // then
            then(cacheManager).should().getCache(CACHE_NAME);
        }
    }

    @Nested
    @DisplayName("clear")
    class ClearTest {

        @Mock
        private org.springframework.cache.Cache springCache;

        @Test
        @DisplayName("캐시가 존재하면 모든 항목을 제거한다")
        void clear_withExistingCache_clearsAllEntries() {
            // given
            given(cacheManager.getCache(CACHE_NAME)).willReturn(springCache);

            // when
            cacheService.clear(CACHE_NAME);

            // then
            then(springCache).should().clear();
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        @DisplayName("캐시 이름이 null이면 아무 작업도 수행하지 않는다")
        void clear_withNullCacheName_doesNothing() {
            // when
            cacheService.clear(null);

            // then
            then(cacheManager).should(never()).getCache(null);
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
}
