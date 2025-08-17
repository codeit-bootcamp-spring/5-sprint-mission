package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody MessageCreateRequest request) {
        messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅메시지 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable UUID id) {
        Message found = messageService.findById(id);
        return ResponseEntity.ok(found);
    }

    // ✅채널 내 모든 메시지 조회
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Message>> findAllByChannelId(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    // ✅메시지 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody MessageUpdateRequest request) {
        messageService.update(request);
        return ResponseEntity.ok().build();
    }

    // ✅메시지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Message target = messageService.findById(id); // 삭제 전 존재 확인
        messageService.delete(target);
        return ResponseEntity.noContent().build();
    }
}
