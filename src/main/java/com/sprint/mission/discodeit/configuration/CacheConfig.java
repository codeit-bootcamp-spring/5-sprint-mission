package com.sprint.mission.discodeit.configuration;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
		ObjectMapper redisObjectMapper = objectMapper.copy();
		redisObjectMapper.activateDefaultTyping(
			LaissezFaireSubTypeValidator.instance,
			ObjectMapper.DefaultTyping.EVERYTHING,
			As.PROPERTY
		);

		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new GenericJackson2JsonRedisSerializer(redisObjectMapper)
				)
			)
			.prefixCacheNameWith("discodeit:")
			.entryTtl(Duration.ofMinutes(10))
			.disableCachingNullValues();
	}
}
