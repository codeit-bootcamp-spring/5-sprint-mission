package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("channels", "notification", "users", "user");
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(10 + ThreadLocalRandom.current().nextInt(-2, 3)))
                        .expireAfterAccess(Duration.ofMinutes(5))
                        .removalListener((key, value, cause) ->
                                log.info("removalListener call! key={} value={} cause={}", key, value, cause))
                        // .recordStats()는 프로덕션에서 성능/모니터링 필요시 활성화하세요
                        .recordStats()
        );
        return manager;
    }

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                DefaultTyping.EVERYTHING,
                As.PROPERTY
        );

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                                )
                        )
                        .prefixCacheNameWith("cache-demo:")
                        .entryTtl(Duration.ofSeconds(600))
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .initialCacheNames(Set.of("users", "user", "channels", "notification"))
                .build();
    }

//    // 이렇게 했을 때, 만약 caffein과 redis에 동일한 이름의 캐시가 존재하면
//    // caffein -> redis 순서대로 확인해서 첫 번째에서 해당 캐시를 반환한다.
//    // 현재는 같은 이름이 있으면 caffein만 사용되고 redis는 무시된다.
//    // 아래의 분리 로직을 적용하면 l1에서 미스가 되면 l2에서 읽어와 l1에 채우는 로직이 구현된다.
//    // 멀티레벨 캐시 매니저 단순히 2개를 구성하고 싶을떄
//    @Bean
//    public CacheManager cacheManager(
//            RedisConnectionFactory connectionFactory,
//            ObjectMapper objectMapper
//    ) {
//        return new CompositeCacheManager(caffeineCacheManager(),
//                redisCacheManager(connectionFactory, objectMapper)); // L1 → L2 순서
//    }

    // L1, L2를 완전히 분리하고 싶을때
    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        CaffeineCacheManager caffeine = caffeineCacheManager();
        RedisCacheManager redis = redisCacheManager(connectionFactory, objectMapper);

        // L1+L2 체인으로 묶을 캐시 이름
        // L1+L2 캐시를 사용하는 건 속도측면에서는 좋을 수 있지만, 구현하기 어렵고 일관성 문제 등이 있어,
        // 실제로 구현하기 보다는 CDN 캐싱 또는 Gateway 캐시(reverse Proxy 캐시)를 사용한다?
        Set<String> chainCaches = Set.of("users", "user", "channels", "notification");

        return new CodeitLevelCacheManager(caffeine, redis, chainCaches);
    }
}
