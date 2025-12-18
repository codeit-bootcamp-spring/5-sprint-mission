package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredTopicListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "discodeit.MessageCreatedEvent",
            containerFactory = "websocketKafkaListenerContainerFactory"
    )
    public void handleMessage(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            log.info("[WebSocketKafka] MessageCreatedEvent 수신 - messageId: {}, channelId: {}",
                    event.getMessage().getId(), event.getMessage().getChannelId());

            String dest = String.format("/sub/channels.%s.messages", event.getMessage().getChannelId());

            messagingTemplate.convertAndSend(dest, event.getMessage());

            log.info("[WebSocketKafka] WebSocket 메시지 전송 완료 - destination: {}, messageId: {}",
                    dest, event.getMessage().getId());

        } catch (Exception e) {
            log.error("[WebSocketKafka] 메시지 처리 실패", e);
        }
    }
}