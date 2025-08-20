package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<Message> createMessage(
            @RequestBody MessageCreateRequest request
    ){
        Message message = messageService.create(request);
        return ResponseEntity.ok(message);
    }

    @RequestMapping(path = "/update/{messageId}", method = RequestMethod.PUT)
    public ResponseEntity<Message> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ){
        Message updatedMessage = messageService.update(messageId, request);
        return ResponseEntity.ok(updatedMessage);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Message> deleteMessage(
            @RequestParam("messageId") UUID messageId
    ){
        Message message = messageService.find(messageId);
        messageService.delete(messageId);
        return ResponseEntity.ok(message);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findMessage(
            @RequestParam("channelId") UUID channelId
    ){
        List<Message> message = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(message);
    }

}
