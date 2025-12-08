package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.ChannelDeletedEvent;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.message.application.MessageCleanupFacade;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelCleanupFacade {

    private final CacheHelper cacheHelper;

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final MessageCleanupFacade messageCleanupFacade;

    @Transactional
    public void cleanup(ChannelDeletedEvent event) {
        UUID channelId = event.channelId();

        log.info("Starting ChannelCleanup: [channelId={}]", channelId);

        try {
            Set<UUID> messageIds = messageRepository.findIdsByChannelId(channelId);
            List<UUID> participantIds = readStatusRepository.findAllByChannelId(channelId).stream()
                .map(readStatus -> readStatus.getUser().getId())
                .toList();

            long deletedReadStatuses = readStatusRepository.deleteByChannelId(channelId);

            participantIds.forEach(participantId -> {
                cacheHelper.evictCacheByKey(CacheName.READ_STATUSES, participantId);
                cacheHelper.evictCacheByKey(CacheName.SUBSCRIBED_CHANNELS, participantId);
            });

            long deletedMessages = messageRepository.deleteAllByChannelId(channelId);

            List<MessageDeletedEvent> messageDeletedEvents = messageIds.stream()
                .map(MessageDeletedEvent::new)
                .toList();
            messageCleanupFacade.cleanupBatch(messageDeletedEvents);

            log.debug("ChannelCleanup completed: [channelId={}, deletedMessages={}, deletedReadStatuses={}]",
                channelId, deletedMessages, deletedReadStatuses);
        } catch (Exception e) {
            log.error("ChannelCleanup failed: [channelId={}]", channelId, e);
            throw e;
        }
    }
}
