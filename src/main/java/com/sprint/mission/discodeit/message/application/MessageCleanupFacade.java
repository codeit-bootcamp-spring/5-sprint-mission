package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageCleanupFacade {

    private final MessageAttachmentRepository messageAttachmentRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final ApplicationEventPublisher eventPublisher;

    // 가벼워서 그냥 서비스에서 바로 처리해도 될 것 같다.
    @Transactional
    public void cleanup(MessageDeletedEvent event) {
        UUID messageId = event.messageId();

        log.info("Starting MessageCleanup: [messageId={}]", messageId);

        try {
            Set<UUID> attachmentIds =
                messageAttachmentRepository.findAttachmentIdSetByMessageId(messageId);

            if (attachmentIds.isEmpty()) {
                log.debug("No message attachment found: [messageId={}]", messageId);
                return;
            }

            int deletedAttachmentCount = messageAttachmentRepository.deleteAllByAttachmentIdIn(attachmentIds);
            binaryContentRepository.deleteAllByIdInBatch(attachmentIds);

            log.info("MessageCleanup completed: [messageId={}, deletedAttachments={}]",
                messageId, deletedAttachmentCount);
        } catch (Exception e) {
            log.error("MessageCleanup failed: [messageId={}]", messageId, e);
            throw e;
        }
    }
}
