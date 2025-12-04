package com.sprint.mission.discodeit.configuration;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager(
			"channels", "notifications", "users"
		);

		manager.setCaffeine(Caffeine.newBuilder()
			.maximumSize(10_000)
			.expireAfterWrite(Duration.ofMinutes(10 + ThreadLocalRandom.current().nextInt(-2, 3)))
			.expireAfterAccess(Duration.ofMinutes(5))
			.recordStats()
			.removalListener((key, value, cause) ->
				log.info("removalListener call: key={}, value={}, cause={}", key, value, cause))
			.evictionListener((key, value, cause) ->
				log.info("evictionListener call: key={}, value={}, cause={}", key, value, cause))
		);

		return manager;
	}
}
