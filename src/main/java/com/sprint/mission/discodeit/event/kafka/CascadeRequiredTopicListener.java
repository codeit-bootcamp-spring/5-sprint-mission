package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CascadeRequiredTopicListener {

    private final BinaryContentRepository binaryContentRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageDeletedEvent")
    public void onMessageDeletedEvent(String kafkaEvent) {
        try {
            MessageDeletedEvent event = objectMapper.readValue(kafkaEvent, MessageDeletedEvent.class);
            UUID messageId = event.messageId();

            log.debug("MessageDeletedEvent 수신: messageId={}", messageId);

            List<MessageAttachment> messageAttachments =
                messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(messageId);

            if (messageAttachments.isEmpty()) {
                log.debug("삭제할 첨부파일 없음: messageId={}", messageId);
                return;
            }

            List<UUID> attachmentIds = messageAttachments.stream()
                .map(ma -> ma.getAttachment().getId())
                .toList();

            messageAttachmentRepository.deleteAll(messageAttachments);
            binaryContentRepository.deleteAllByIdInBatch(attachmentIds);

            log.info("메시지 첨부파일 캐스케이드 삭제 완료: messageId={}, count={}", messageId, attachmentIds.size());
        } catch (JsonProcessingException e) {
            log.error("MessageDeletedEvent 역직렬화 실패: {}", kafkaEvent, e);
        }
    }
}
