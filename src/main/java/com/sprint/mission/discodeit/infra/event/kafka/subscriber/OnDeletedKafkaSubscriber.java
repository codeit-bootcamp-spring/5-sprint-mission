package com.sprint.mission.discodeit.infra.event.kafka.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.MessageAttachment;
import com.sprint.mission.discodeit.domain.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.domain.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.domain.event.user.UserDeletedEvent;
import com.sprint.mission.discodeit.domain.facade.UserCleanupFacade;
import com.sprint.mission.discodeit.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.domain.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.domain.repository.MessageRepository;
import com.sprint.mission.discodeit.domain.repository.NotificationRepository;
import com.sprint.mission.discodeit.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.service.NotificationService;
import com.sprint.mission.discodeit.infra.cache.CacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnDeletedKafkaSubscriber {

    private final UserCleanupFacade userCleanupFacade;

    private final BinaryContentRepository binaryContentRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final CacheHelper cacheHelper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageDeletedEvent")
    public void onMessageDeletedEvent(MessageDeletedEvent event) {
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
    }

    @KafkaListener(topics = "discodeit.ChannelDeletedEvent")
    public void onChannelDeletedEvent(ChannelDeletedEvent event) {
        UUID channelId = event.channelId();

        log.debug("ChannelDeletedEvent 수신: channelId={}", channelId);

        List<Message> messages = messageRepository.findByChannelId(channelId);
        List<UUID> participantIds = readStatusRepository.findAllByChannelId(channelId).stream()
            .map(rs -> rs.getUser().getId())
            .toList();

        readStatusRepository.deleteByChannelId(channelId);

        participantIds.forEach(participantId -> cacheHelper.evictCacheByKey("readStatuses", participantId));

        for (Message message : messages) {
            eventPublisher.publishEvent(new MessageDeletedEvent(message.getId()));
        }
        messageRepository.deleteAll(messages);

        log.info("채널 캐스케이드 삭제 완료: channelId={}, messageCount={}", channelId, messages.size());
    }

    @RetryableTopic(
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "discodeit.UserDeletedEvent", groupId = "user-cleanup-group")
    public void listen(UserDeletedEvent event) {
        userCleanupFacade.cleanup(event);
    }

    @DltHandler
    public void handleDlt(UserDeletedEvent event) {
        log.error("Cleanup 최종 실패. 관리자 확인 필요. UserID: {}, Payload: {}", event.userId(), event);
    }
}
