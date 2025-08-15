package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageSendRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(path = "/channels/{channelId}/messages")
    public ResponseEntity<MessageResponse> send(@PathVariable("channelId") UUID channelId,
                                                @Valid @RequestBody MessageSendRequest body) {
        MessageResponse res = messageService.send(channelId, body);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(res.id()).toUri();
        return ResponseEntity.created(location).body(res);
    }

    @PutMapping(path = "/messages/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") UUID id,
                                       @Valid @RequestBody MessageUpdateRequest body) {
        messageService.update(id, body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/messages/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id,
                                       @RequestParam("actorId") UUID actorId) {
        messageService.delete(id, actorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/channels/{channelId}/messages")
    public ResponseEntity<List<MessageResponse>> list(@PathVariable("channelId") UUID channelId,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "size", required = false) Integer size) {
        return ResponseEntity.ok(messageService.findByChannel(channelId, page, size));
    }

    @GetMapping(path = "/messages/{id}")
    public ResponseEntity<MessageResponse> find(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(messageService.find(id));
    }
}
