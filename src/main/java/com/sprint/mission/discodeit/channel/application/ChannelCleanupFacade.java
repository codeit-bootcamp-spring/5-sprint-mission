package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.ChannelDeletedEvent;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.message.application.MessageCleanupFacade;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelCleanupFacade {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageCleanupFacade messageCleanupFacade;

    private final CacheHelper cacheHelper;

    @Transactional
    public void cleanup(ChannelDeletedEvent event) {
        UUID channelId = event.channelId();

        log.debug("Starting ChannelCleanup: [channelId={}]", channelId);

        try {
            Set<UUID> messageIds = messageRepository.findIdsByChannelId(channelId);
            Set<UUID> participantIds = readStatusRepository.findUserIdsByChannelId(channelId);

            long deletedReadStatuses = readStatusRepository.deleteByChannelId(channelId);
            long deletedMessages = messageRepository.deleteByChannelId(channelId);

            if (!participantIds.isEmpty()) {
                // cacheHelper.evictAll(CacheName.READ_STATUSES, participantIds);
                // cacheHelper.evictAll(CacheName.SUBSCRIBED_CHANNELS, participantIds);
            }

            if (!messageIds.isEmpty()) {
                List<MessageDeletedEvent> messageEvents = messageIds.stream()
                    .map(MessageDeletedEvent::new)
                    .toList();
                messageCleanupFacade.cleanupBatch(messageEvents);
            }

            log.info("ChannelCleanup completed: [channelId={}, deletedMessages={}, deletedReadStatuses={}]",
                channelId, deletedMessages, deletedReadStatuses);
        } catch (Exception e) {
            log.error("ChannelCleanup failed: [channelId={}]", channelId, e);
            throw e;
        }
    }
}
