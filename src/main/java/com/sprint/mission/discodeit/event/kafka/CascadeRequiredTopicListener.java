package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

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

    @KafkaListener(topics = "discodeit.MessageDeletedEvent")
    public void onChannelDeletedEvent(String kafkaEvent) {
        try {
            ChannelDeletedEvent event = objectMapper.readValue(kafkaEvent, ChannelDeletedEvent.class);
            UUID channelId = event.channelId();

            log.debug("ChannelDeletedEvent 수신: channelId={}", channelId);

            List<Message> messages = messageRepository.findByChannelId(channelId);

            int readStatusCount = readStatusRepository.deleteByChannelId(channelId);

            for (Message message : messages) {
                applicationEventPublisher.publishEvent(new MessageDeletedEvent(message.getId()));
            }

            log.info("채널 관련 캐스케이드 삭제 완료: channelId={}, messageCount={}, readStatusCount={}",
                channelId, messages.size(), readStatusCount);
        } catch (JsonProcessingException e) {
            log.error("ChannelDeletedEvent 역직렬화 실패: {}", kafkaEvent, e);
        }
    }
}
