package com.sprint.mission.discodeit.infrastructure.cache;

import com.sprint.mission.discodeit.global.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService implements CacheService {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("Redis evict: [cache={}, key={}]", cacheName, key);
        }
    }

    @Override
    public void evictAll(String cacheName, Collection<?> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<String> fullKeys = keys.stream()
            .map(key -> generateKey(cacheName, key))
            .toList();

        Long deletedCount = redisTemplate.delete(fullKeys);
        log.debug("Redis evictAll: [cache={}, keyCount={}, deleted={}]",
            cacheName, keys.size(), deletedCount);
    }

    @Override
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("Redis clear: cache=[{}]", cacheName);
        }
    }

    private String generateKey(String cacheName, Object key) {
        String prefix = cacheProperties.getRedis().getKeyPrefix();
        return prefix + cacheName + "::" + key.toString();
    }
}
