package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelCleanupFacade {

    private static final int BATCH_SIZE = 1000;

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    private final CacheService cacheService;

    @Transactional
    public void cleanup(ChannelDeletedEvent event) {
        UUID channelId = event.channelId();
        ChannelType channelType = event.channelType();

        log.debug("Starting ChannelCleanup: [channelType={}, channelId={}]", channelType, channelId);

        try {
            Set<UUID> messageIds = messageRepository.findIdSetByChannelId(channelId);
            Set<UUID> participantIds = readStatusRepository.findUserIdSetByChannelId(channelId);

            int deletedReadStatusesCount = readStatusRepository.deleteAllByChannelId(channelId);

            if (!messageIds.isEmpty()) {
                cleanupAttachmentsInBatch(messageIds);
            }

            int deletedMessagesCount = messageRepository.deleteAllByChannelId(channelId);

            cacheService.evictAll(CacheName.READ_STATUSES, participantIds);
            if (channelType == ChannelType.PUBLIC) {
                cacheService.clear(CacheName.PUBLIC_CHANNELS);
            } else {
                cacheService.evictAll(CacheName.SUBSCRIBED_CHANNELS, participantIds);
            }

            log.info("ChannelCleanup completed: [channelId={}, deletedMessages={}, deletedReadStatuses={}]",
                channelId, deletedMessagesCount, deletedReadStatusesCount);
        } catch (Exception e) {
            log.error("ChannelCleanup failed: [channelId={}]", channelId, e);
            throw e;
        }
    }

    private void cleanupAttachmentsInBatch(Set<UUID> messageIds) {
        List<UUID> allMessageIds = new ArrayList<>(messageIds);
        int totalSize = allMessageIds.size();

        for (int i = 0; i < totalSize; i += BATCH_SIZE) {
            int end = Math.min(totalSize, i + BATCH_SIZE);
            List<UUID> batchMessageIds = allMessageIds.subList(i, end);

            Set<UUID> batchAttachmentIds =
                messageAttachmentRepository.findAttachmentIdSetByMessageIdIn(batchMessageIds);

            if (!batchAttachmentIds.isEmpty()) {
                messageAttachmentRepository.deleteAllByMessageIdIn(batchMessageIds);
                binaryContentRepository.deleteAllByIdInBatch(batchAttachmentIds);

                log.debug("Processed batch attachment cleanup: {}/{}", end, totalSize);
            }
        }
    }
}
