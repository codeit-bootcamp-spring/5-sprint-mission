package com.sprint.mission.discodeit.infra.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CacheHelperTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CacheHelper cacheHelper;

    @Test
    @DisplayName("유효한 캐시명과 키로 evict 호출 시 캐시에서 해당 키를 제거한다")
    void evictCacheByKey_withValidCacheNameAndKey_evictsFromCache() {
        // given
        String cacheName = "testCache";
        Object key = "testKey";
        given(cacheManager.getCache(cacheName)).willReturn(cache);

        // when
        cacheHelper.evictCacheByKey(cacheName, key);

        // then
        then(cache).should().evict(key);
    }

    @Test
    @DisplayName("캐시명이 null이거나 비어있으면 evict를 호출하지 않는다")
    void evictCacheByKey_withNullCacheName_doesNothing() {
        // when
        cacheHelper.evictCacheByKey(null, "testKey");
        cacheHelper.evictCacheByKey(" ", "testKey");

        // then
        then(cacheManager).should(never()).getCache("testKey");
    }

    @Test
    @DisplayName("키가 null이면 evict를 호출하지 않는다")
    void evictCacheByKey_withNullKey_doesNothing() {
        // given
        String cacheName = "testCache";
        Object key = null;

        // when
        cacheHelper.evictCacheByKey(cacheName, key);

        // then
        then(cacheManager).should(never()).getCache(cacheName);
    }

    @Test
    @DisplayName("캐시가 존재하지 않으면 evict를 호출하지 않는다")
    void evictCacheByKey_withNonExistentCache_doesNothing() {
        // given
        String cacheName = "nonExistentCache";
        Object key = "testKey";
        given(cacheManager.getCache(cacheName)).willReturn(null);

        // when
        cacheHelper.evictCacheByKey(cacheName, key);

        // then
        then(cache).should(never()).evict(key);
    }
}
