package com.sprint.mission.discodeit.infra.event.kafka;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.MessageAttachment;
import com.sprint.mission.discodeit.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.domain.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.domain.repository.MessageRepository;
import com.sprint.mission.discodeit.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.service.NotificationService;
import com.sprint.mission.discodeit.infra.cache.CacheHelper;
import com.sprint.mission.discodeit.infra.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.infra.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.infra.event.user.UserDeletedEvent;
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
public class CascadeEventConsumer {

    private final BinaryContentRepository binaryContentRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CacheHelper cacheHelper;

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
                applicationEventPublisher.publishEvent(new MessageDeletedEvent(message.getId()));
            }
            messageRepository.deleteAll(messages);

            log.info("채널 캐스케이드 삭제 완료: channelId={}, messageCount={}", channelId, messages.size());
    }

    @KafkaListener(topics = "discodeit.UserDeletedEvent")
    public void onUserDeletedEvent(UserDeletedEvent event) {
            UUID userId = event.userId();

            log.debug("UserDeletedEvent 수신: userId={}", userId);

            messageRepository.nullifyAuthorByUserId(userId);
            notificationService.deleteByReceiverId(userId);
            readStatusRepository.deleteByUserId(userId);
            cacheHelper.evictCacheByKey("readStatuses", userId);

            log.info("유저 캐스케이드 삭제 완료: userId={}", userId);
    }
}
