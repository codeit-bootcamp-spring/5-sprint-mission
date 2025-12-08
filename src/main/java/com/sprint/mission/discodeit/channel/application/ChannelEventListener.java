package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.PrivateChannelCreatedEvent;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelEventListener {

    private final CacheService cacheService;

    @Async
    @TransactionalEventListener
    public void on(PrivateChannelCreatedEvent event) {
        for (UUID userId : event.participantIds()) {
            cacheService.evict(CacheName.READ_STATUSES, userId);
            cacheService.evict(CacheName.SUBSCRIBED_CHANNELS, userId);
        }
    }
}
