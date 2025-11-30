package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sprint.mission.discodeit.config.properties.CacheProperties;
import com.sprint.mission.discodeit.config.properties.CacheProperties.CacheSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(buildCaffeine(cacheProperties.defaultSpec()));

        cacheProperties.caches().forEach((cacheName, spec) ->
            caffeineCacheManager.registerCustomCache(cacheName, buildCaffeine(spec).build())
        );

        return caffeineCacheManager;
    }

    private Caffeine<Object, Object> buildCaffeine(CacheSpec spec) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .recordStats()
            .maximumSize(spec.maximumSize());

        if (spec.expireAfterAccess() == null && spec.expireAfterWrite() == null) {
            caffeine.expireAfterAccess(Duration.ofMinutes(5));
        } else {
            if (spec.expireAfterAccess() != null) {
                caffeine.expireAfterAccess(spec.expireAfterAccess());
            }
            if (spec.expireAfterWrite() != null) {
                caffeine.expireAfterWrite(spec.expireAfterWrite());
            }
        }

        return caffeine;
    }
}
