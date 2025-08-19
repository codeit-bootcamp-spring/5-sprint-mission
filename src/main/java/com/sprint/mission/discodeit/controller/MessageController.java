package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> create(@RequestBody MessageCreateRequest req) {
        if (req == null || req.channelId() == null || req.authorId() == null) {
            throw new IllegalArgumentException("channelId와 authorId는 필수");
        }
        Message saved = messageService.create(req, List.of());
        return ResponseEntity.created(URI.create("/api/messages/" + saved.getId())).body(saved);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{messageId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> find(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.find(messageId));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/by-channel/{channelId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Message>> findByChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{messageId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(@PathVariable UUID messageId,
                                          @RequestBody MessageUpdateRequest req) {
        if (req == null) throw new IllegalArgumentException("Empty");
        Message updated = messageService.update(messageId, req);
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}
