package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        log.info("[WebSocketRequiredEventListener] MessageCreatedEvent 수신 - messageId: {}, channelId: {}",
                event.getMessage().getId(), event.getMessage().getChannelId());

        String dest = String.format("/sub/channels.%s.messages", event.getMessage().getChannelId());

        messagingTemplate.convertAndSend(dest, event.getMessage());

        log.info("[WebSocketRequiredEventListener] WebSocket 메시지 전송 완료 - destination: {}, messageId: {}",
                dest, event.getMessage().getId());
    }

}
