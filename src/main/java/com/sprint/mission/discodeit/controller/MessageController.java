package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    // 생성자 주입
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ✅메시지 생성
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Message created = messageService.create(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    // ✅메시지 단건 조회
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Message> findById(@PathVariable UUID id) {
        Message found = messageService.findById(id);
        return ResponseEntity.ok(found);
    }

    // ✅채널 내 모든 메시지 조회
    @RequestMapping(value = "/findAllByChannelId", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    // ✅메시지 수정
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<Message> update(@RequestBody MessageUpdateRequest request) {
        Message updated = messageService.update(request);
        return ResponseEntity.ok(updated);
    }


    // ✅메시지 삭제
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

}
