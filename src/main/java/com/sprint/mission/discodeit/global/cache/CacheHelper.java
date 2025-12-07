package com.sprint.mission.discodeit.global.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheHelper {

    private final CacheManager cacheManager;

    public void evictCacheByKey(String cacheName, Object key) {
        if (!hasText(cacheName) || key == null) {
            log.warn("Cache key or value is empty");
            return;
        }

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache not found: {}", cacheName);
            return;
        }
        cache.evict(key);
    }
}
