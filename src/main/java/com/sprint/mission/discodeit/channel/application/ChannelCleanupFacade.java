package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelDeletedEvent;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachment;
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
    private final ReadStatusRepository readStatusRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final CacheService cacheService;

    @Transactional
    public void cleanup(ChannelDeletedEvent event) {
        UUID channelId = event.channelId();

        log.debug("Starting ChannelCleanup: [channelId={}]", channelId);

        try {
            Set<UUID> messageIds = messageRepository.findIdsByChannelId(channelId);
            Set<UUID> participantIds = readStatusRepository.findUserIdsByChannelId(channelId);

            if (!participantIds.isEmpty()) {
                cacheService.evictAll(CacheName.READ_STATUSES, participantIds);
                cacheService.evictAll(CacheName.SUBSCRIBED_CHANNELS, participantIds);
            }

            long deletedReadStatuses = readStatusRepository.deleteByChannelId(channelId);

            if (!messageIds.isEmpty()) {
                cleanupAttachmentsInBatches(messageIds);
            }

            long deletedMessages = messageRepository.deleteByChannelId(channelId);

            log.info("ChannelCleanup completed: [channelId={}, deletedMessages={}, deletedReadStatuses={}]",
                channelId, deletedMessages, deletedReadStatuses);
        } catch (Exception e) {
            log.error("ChannelCleanup failed: [channelId={}]", channelId, e);
            throw e;
        }
    }

    private void cleanupAttachmentsInBatches(Set<UUID> messageIds) {
        List<UUID> allMessageIds = new ArrayList<>(messageIds);
        int totalSize = allMessageIds.size();

        for (int i = 0; i < totalSize; i += BATCH_SIZE) {
            int end = Math.min(totalSize, i + BATCH_SIZE);
            List<UUID> batchMessageIds = allMessageIds.subList(i, end);

            List<MessageAttachment> attachments =
                messageAttachmentRepository.findAllByMessageIdIn(batchMessageIds);

            if (!attachments.isEmpty()) {
                List<UUID> attachmentIds = attachments.stream()
                    .map(ma -> ma.getAttachment().getId())
                    .toList();

                messageAttachmentRepository.deleteAllInBatch(attachments);
                binaryContentRepository.deleteAllByIdInBatch(attachmentIds);

                log.debug("Processed batch attachment cleanup: {}/{}", end, totalSize);
            }
        }
    }
}
