package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sprint.mission.discodeit.config.properties.CacheProperties;
import com.sprint.mission.discodeit.config.properties.CacheProperties.CacheSpec;
import com.sprint.mission.discodeit.config.properties.CacheProperties.DefaultSpec;
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
        DefaultSpec defaultSpec = cacheProperties.defaultSpec();

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .recordStats()
            .maximumSize(defaultSpec.maximumSize())
            .expireAfterAccess(defaultSpec.expireAfterAccess()));

        if (cacheProperties.caches() != null) {
            cacheProperties.caches().forEach((name, spec) ->
                cacheManager.registerCustomCache(name, buildCaffeine(spec, defaultSpec).build())
            );
        }

        return cacheManager;
    }

    private Caffeine<Object, Object> buildCaffeine(CacheSpec spec, DefaultSpec defaultSpec) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().recordStats();

        builder.maximumSize(spec.maximumSize() != null ? spec.maximumSize() : defaultSpec.maximumSize());

        if (spec.expireAfterWrite() != null) {
            builder.expireAfterWrite(spec.expireAfterWrite());
        } else if (spec.expireAfterAccess() != null) {
            builder.expireAfterAccess(spec.expireAfterAccess());
        } else {
            builder.expireAfterAccess(defaultSpec.expireAfterAccess());
        }

        return builder;
    }
}
