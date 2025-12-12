package com.sprint.mission.discodeit.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import static org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import static org.springframework.util.StringUtils.hasText;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory,
        ObjectMapper objectMapper
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        ObjectMapper redisObjectMapper = createRedisObjectMapper(objectMapper);
        GenericJackson2JsonRedisSerializer jsonRedisSerializer =
            new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        return template;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        ObjectMapper redisObjectMapper = createRedisObjectMapper(objectMapper);

        Duration ttl = cacheProperties.getRedis().getTimeToLive();
        if (ttl == null) {
            ttl = Duration.ofMinutes(10);
        }

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                )
            )
            .entryTtl(ttl);

        String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
        if (hasText(keyPrefix)) {
            config = config.prefixCacheNameWith(keyPrefix);
        }

        if (!cacheProperties.getRedis().isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        return config;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public CacheManager redisCacheManager(
        RedisConnectionFactory connectionFactory,
        RedisCacheConfiguration redisCacheConfiguration
    ) {
        RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfiguration);

        List<String> cacheNames = cacheProperties.getCacheNames();
        if (cacheNames != null && !cacheNames.isEmpty()) {
            builder.initialCacheNames(new HashSet<>(cacheNames));
        }

        if (cacheProperties.getRedis().isEnableStatistics()) {
            builder.enableStatistics();
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(cacheProperties.getCacheNames());
        caffeineCacheManager.setCacheSpecification(cacheProperties.getCaffeine().getSpec());
        return caffeineCacheManager;
    }

    @SuppressWarnings("deprecation")
    private ObjectMapper createRedisObjectMapper(ObjectMapper objectMapper) {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            DefaultTyping.EVERYTHING,
            As.PROPERTY
        );
        return redisObjectMapper;
    }
}
