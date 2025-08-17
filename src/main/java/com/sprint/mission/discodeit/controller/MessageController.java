package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(@Qualifier("basicMessageService") MessageService messageService) {
        this.messageService = messageService;
    }

    // 메시지 전송
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> createMessage(@RequestBody MessageCreateRequest request) {
        MessageResponse response = messageService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메시지 수정
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    public ResponseEntity<MessageResponse> updateMessage(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request) {
        if (request.getId() == null || !messageId.equals(request.getId())) {
            throw new IllegalArgumentException("Message ID in path does not match ID in request body.");
        }
        MessageResponse response = messageService.update(request);
        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}
