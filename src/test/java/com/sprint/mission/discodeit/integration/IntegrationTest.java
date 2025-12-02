package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.config.TestKafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestKafkaConfig.class)
public abstract class IntegrationTest {

    @Autowired
    protected CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
