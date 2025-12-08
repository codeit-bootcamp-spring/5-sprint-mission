package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.dto.PrivateChannelCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.dto.PublicChannelUpdatedEvent;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChannelCacheEvictListener {

    private final CacheService cacheService;

    @Async
    @TransactionalEventListener
    public void on(PrivateChannelCreatedEvent event) {
        cacheService.evictAll(CacheName.READ_STATUSES, event.participantIds());
        cacheService.evictAll(CacheName.SUBSCRIBED_CHANNELS, event.participantIds());
    }

    @Async
    @TransactionalEventListener
    public void on(PublicChannelUpdatedEvent event) {
        cacheService.clear(CacheName.PUBLIC_CHANNELS);
    }
}
