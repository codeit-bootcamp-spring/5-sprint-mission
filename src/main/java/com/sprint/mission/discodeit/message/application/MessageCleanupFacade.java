package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachment;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageCleanupFacade {

    private final MessageAttachmentRepository messageAttachmentRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void cleanup(MessageDeletedEvent event) {
        UUID messageId = event.messageId();

        log.info("Starting ChannelCleanup: [messageId={}]", messageId);

        try {
            List<MessageAttachment> messageAttachments =
                messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(messageId);

            if (messageAttachments.isEmpty()) {
                log.debug("No message attachment found: [messageId={}]", messageId);
                return;
            }

            List<UUID> attachmentIds = messageAttachments.stream()
                .map(ma -> ma.getAttachment().getId())
                .toList();

            messageAttachmentRepository.deleteAll(messageAttachments);
            binaryContentRepository.deleteAllByIdInBatch(attachmentIds);

        } catch (Exception e) {
            log.error("MessageCleanup failed: [messageId={}]", messageId, e);
            throw e;
        }
    }

    @Transactional
    public void cleanupBatch(List<MessageDeletedEvent> events) {
        for (MessageDeletedEvent event : events) {
            cleanup(event);
        }
    }
}
