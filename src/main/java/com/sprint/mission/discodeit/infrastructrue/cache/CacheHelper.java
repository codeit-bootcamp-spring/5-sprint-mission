package com.sprint.mission.discodeit.infrastructrue.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final CacheManager cacheManager;

    public void evictCacheByKey(String cacheName, Object key) {
        if (!hasText(cacheName) || key == null) {
            return;
        }

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
