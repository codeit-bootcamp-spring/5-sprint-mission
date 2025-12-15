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
        if (key == null) {
            return;
        }
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("Caffeine evict: [cache={}, key={}]", cacheName, key);
        }
    }

    @Override
    public void evictAll(String cacheName, Collection<?> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }

        Object nativeCache = cache.getNativeCache();

        if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
            @SuppressWarnings("unchecked")
            com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) nativeCache;
            caffeineCache.invalidateAll(keys);
            log.debug("Caffeine evictAll: [cache={}, keyCount={}]", cacheName, keys.size());
        } else {
            keys.forEach(cache::evict);
            log.warn("Caffeine evictAll fallback: [cache={}, keyCount={}] - not a Caffeine instance",
                cacheName, keys.size());
        }
    }

    @Override
    public void clear(String cacheName) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("Caffeine clear: [cache={}]", cacheName);
        }
    }

    private Cache getCache(String cacheName) {
        if (!hasText(cacheName)) {
            return null;
        }
        return cacheManager.getCache(cacheName);
    }
}
