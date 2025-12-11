package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/pub/messages")
    public void publishMessage(MessageCreateRequest request) {
        log.info("[WebSocket-Controller] WebSocket 메시지 수신: channel_id: {}, author_id: {}, content: {}"
                , request.getChannelId(), request.getAuthorId(), request.getContent());

        messageService.create(request);
    }

}
