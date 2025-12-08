package com.sprint.mission.discodeit.infrastructure.cache;

import com.sprint.mission.discodeit.global.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static org.springframework.util.StringUtils.hasText;

@Service
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class CaffeineCacheService implements CacheService {

    private final CacheManager cacheManager;

    @Override
    public void evict(String cacheName, Object key) {
        Cache cache = getCache(cacheName);
        if (cache != null && key != null) {
            cache.evict(key);
            log.debug("Evicted key [{}] from cache [{}]", key, cacheName);
        }
    }

    @Override
    public void evictAll(String cacheName, Collection<?> keys) {
        Cache cache = getCache(cacheName);
        if (cache == null || keys == null || keys.isEmpty()) {
            return;
        }

        Object nativeCache = cache.getNativeCache();

        if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
            @SuppressWarnings("unchecked")
            com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) nativeCache;

            caffeineCache.invalidateAll(keys);
            log.debug("Caffeine bulk evict: {} keys from [{}]", keys.size(), cacheName);
        } else {
            keys.forEach(cache::evict);
            log.warn("Not a Caffeine cache instance. Fallback to loop eviction.");
        }
    }

    @Override
    public void clear(String cacheName) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("Cleared all entries from cache [{}]", cacheName);
        }
    }

    private Cache getCache(String cacheName) {
        if (!hasText(cacheName)) {
            return null;
        }
        return cacheManager.getCache(cacheName);
    }
}
