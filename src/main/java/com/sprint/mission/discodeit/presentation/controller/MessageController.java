package com.sprint.mission.discodeit.presentation.controller;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.presentation.api.MessageApi;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController implements MessageApi {
    private final MessageService messageService;

    @RequestMapping(method = POST)
    public ResponseEntity<MessageResponse> create(@RequestBody CreateMessageRequest request) {
        MessageResponse created = messageService.createMessage(request);
        if (created == null) throw new NotFoundException("사용자 또는 채널을 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "/{channelId}", method = GET)
    public ResponseEntity<List<MessageResponse>> findAllByChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.getAllByChannelId(channelId));
    }

    @RequestMapping(method = PUT)
    public ResponseEntity<MessageResponse> update(@RequestBody UpdateMessageRequest request) {
        MessageResponse updated = messageService.update(request);
        if (updated == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{messageId}", method = DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        boolean removed = messageService.remove(messageId);
        if (!removed) throw new NotFoundException("메시지를 찾을 수 없습니다.");
        return ResponseEntity.noContent().build();
    }
}
