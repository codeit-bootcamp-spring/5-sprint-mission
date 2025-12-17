package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.security.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class JwtRegistryConfig {

    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @ConditionalOnProperty(name = "discodeit.jwt.registry-type", havingValue = "redis")
    public JwtRegistry redisJwtRegistry(
            RedisTemplate<String, Object> redisTemplate,
            RedisLockProvider redisLockProvider
    ) {
        return new RedisJwtRegistry(
                jwtProperties.getMaxActiveCount(),
                jwtTokenProvider,
                redisTemplate,
                redisLockProvider
        );
    }

    @Bean
    @ConditionalOnProperty(
            name = "discodeit.jwt.registry-type",
            havingValue = "memory",
            matchIfMissing = true
    )
    public JwtRegistry inMemoryJwtRegistry() {
        return new InMemoryJwtRegistry(jwtProperties, jwtTokenProvider);
    }
}