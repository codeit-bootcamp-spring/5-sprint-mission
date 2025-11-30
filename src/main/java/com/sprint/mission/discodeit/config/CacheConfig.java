package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sprint.mission.discodeit.config.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .recordStats()
            .maximumSize(cacheProperties.maximumSize())
            .expireAfterAccess(cacheProperties.expireAfterAccess()));
        return cacheManager;
    }
}
