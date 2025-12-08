package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.PrivateChannelCreatedEvent;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelEventListener {

    private final CacheHelper cacheHelper;

    @Async
    @TransactionalEventListener
    public void on(PrivateChannelCreatedEvent event) {
        for (UUID userId : event.participantIds()) {
            cacheHelper.evictCacheByKey(CacheName.READ_STATUSES, userId);
            cacheHelper.evictCacheByKey(CacheName.SUBSCRIBED_CHANNELS, userId);
        }
    }
}
