package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.*;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseRequiredTopicListener {

    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "discodeit.NotificationCreatedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleNotificationCreated(String kafkaEvent) {
        try {
            NotificationCreatedEvent event = objectMapper.readValue(kafkaEvent, NotificationCreatedEvent.class);

            log.debug("[SseKafka] 알림 생성 이벤트 수신 - receiverId: {}", event.getReceiverId());

            sseService.send(
                    List.of(event.getReceiverId()),
                    "notifications.created",
                    event.getNotificationDto()
            );
        } catch (Exception e) {
            log.error("[SseKafka] NotificationCreated 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.BinaryContentUpdatedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleBinaryContentUpdated(String kafkaEvent) {
        try {
            BinaryContentUpdatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentUpdatedEvent.class);

            log.debug("[SseKafka] 파일 상태 변경 이벤트 수신 - receiverId: {}", event.getReceiverId());

            sseService.send(
                    List.of(event.getReceiverId()),
                    "binaryContents.updated",
                    event.getBinaryContentDto()
            );
        } catch (Exception e) {
            log.error("[SseKafka] BinaryContentUpdated 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.ChannelCreatedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleChannelCreated(String kafkaEvent) {
        try {
            ChannelCreatedEvent event = objectMapper.readValue(kafkaEvent, ChannelCreatedEvent.class);

            log.debug("[SseKafka] 채널 생성 이벤트 수신 - channelId: {}", event.getChannelResponse().getId());

            sseService.broadcast("channels.created", event.getChannelResponse());
        } catch (Exception e) {
            log.error("[SseKafka] ChannelCreated 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.ChannelUpdatedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleChannelUpdated(String kafkaEvent) {
        try {
            ChannelUpdatedEvent event = objectMapper.readValue(kafkaEvent, ChannelUpdatedEvent.class);

            log.debug("[SseKafka] 채널 수정 이벤트 수신 - channelId: {}", event.getChannelResponse().getId());

            sseService.broadcast("channels.updated", event.getChannelResponse());
        } catch (Exception e) {
            log.error("[SseKafka] ChannelUpdated 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.ChannelDeletedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleChannelDeleted(String kafkaEvent) {
        try {
            ChannelDeletedEvent event = objectMapper.readValue(kafkaEvent, ChannelDeletedEvent.class);

            log.debug("[SseKafka] 채널 삭제 이벤트 수신 - channelId: {}", event.getChannelResponse().getId());

            sseService.broadcast("channels.deleted", event.getChannelResponse());
        } catch (Exception e) {
            log.error("[SseKafka] ChannelDeleted 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.UserCreatedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleUserCreated(String kafkaEvent) {
        try {
            UserCreatedEvent event = objectMapper.readValue(kafkaEvent, UserCreatedEvent.class);

            log.debug("[SseKafka] 사용자 생성 이벤트 수신 - userId: {}", event.getUserResponse().getId());

            sseService.broadcast("users.created", event.getUserResponse());
        } catch (Exception e) {
            log.error("[SseKafka] UserCreated 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.UserUpdatedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleUserUpdated(String kafkaEvent) {
        try {
            UserUpdatedEvent event = objectMapper.readValue(kafkaEvent, UserUpdatedEvent.class);

            log.debug("[SseKafka] 사용자 수정 이벤트 수신 - userId: {}", event.getUserResponse().getId());

            sseService.broadcast("users.updated", event.getUserResponse());
        } catch (Exception e) {
            log.error("[SseKafka] UserUpdated 처리 실패", e);
        }
    }

    @KafkaListener(
            topics = "discodeit.UserDeletedEvent",
            containerFactory = "sseKafkaListenerContainerFactory"
    )
    public void handleUserDeleted(String kafkaEvent) {
        try {
            UserDeletedEvent event = objectMapper.readValue(kafkaEvent, UserDeletedEvent.class);

            log.debug("[SseKafka] 사용자 삭제 이벤트 수신 - userId: {}", event.getUserResponse().getId());

            sseService.broadcast("users.deleted", event.getUserResponse());
        } catch (Exception e) {
            log.error("[SseKafka] UserDeleted 처리 실패", e);
        }
    }
}