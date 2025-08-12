package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {

    // TODO 나중에 로그인 중인 사용자만 처리하면 될듯?
    private final MessageService messageService;

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<MessageDto.DetailResponse> createMessage(@ModelAttribute MessageDto.CreateRequest request) {
        return ResponseEntity.ok(messageService.create(request));
    }

    @RequestMapping(value = "/message", method = RequestMethod.PUT)
    public ResponseEntity<MessageDto.DetailResponse> updateMessage(@RequestBody MessageDto.UpdateRequest request) {
        return ResponseEntity.ok(messageService.update(request));
    }

    @RequestMapping(value = "/message/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/message/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto.DetailResponse>> getMessagesByChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
