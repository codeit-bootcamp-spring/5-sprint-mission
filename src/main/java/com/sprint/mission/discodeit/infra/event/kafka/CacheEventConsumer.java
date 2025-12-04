package com.sprint.mission.discodeit.infra.event.kafka;

import com.sprint.mission.discodeit.infra.cache.CacheHelper;
import com.sprint.mission.discodeit.infra.event.cache.CacheEvictEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheEventConsumer {

    private final CacheHelper cacheHelper;

    @KafkaListener(topics = "${discodeit.CacheEvictEvent}")
    public void onCacheEvictEvent(CacheEvictEvent event) {
        log.info("캐시 제거 이벤트 수신: cacheName={}, key={}", event.cacheName(), event.key());
        cacheHelper.evictCacheByKey(event.cacheName(), event.key());
    }
}
