package com.sprint.mission.discodeit.infrastructure.cache;

import com.sprint.mission.discodeit.global.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Service
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@Slf4j
public class RedisCacheService implements CacheService {

    private static final String CACHE_KEY_SEPARATOR = "::";

    private final RedisTemplate<String, Object> redisTemplate;
    private final String keyPrefix;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, CacheProperties cacheProperties) {
        this.redisTemplate = redisTemplate;
        String prefix = cacheProperties.getRedis().getKeyPrefix();
        this.keyPrefix = hasText(prefix) ? prefix : "";
    }

    @Override
    public void evict(String cacheName, Object key) {
        if (!hasText(cacheName) || key == null) {
            return;
        }

        String redisKey = generateRedisKey(cacheName, key);
        redisTemplate.delete(redisKey);

        log.debug("Redis evict: {}", redisKey);
    }

    @Override
    public void evictAll(String cacheName, Collection<?> keys) {
        if (!hasText(cacheName) || keys == null || keys.isEmpty()) {
            return;
        }

        List<String> redisKeys = keys.stream()
            .filter(Objects::nonNull)
            .map(key -> generateRedisKey(cacheName, key))
            .toList();

        if (!redisKeys.isEmpty()) {
            redisTemplate.delete(redisKeys);
            log.debug("Redis bulk evict: {} keys from [{}]", redisKeys.size(), cacheName);
        }
    }

    @Override
    public void clear(String cacheName) {
        if (!hasText(cacheName)) {
            return;
        }

        String pattern = keyPrefix + cacheName + CACHE_KEY_SEPARATOR + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("Redis clear: {} keys from [{}]", keys.size(), cacheName);
        }
    }

    private String generateRedisKey(String cacheName, Object key) {
        return keyPrefix + cacheName + CACHE_KEY_SEPARATOR + key.toString();
    }
}
