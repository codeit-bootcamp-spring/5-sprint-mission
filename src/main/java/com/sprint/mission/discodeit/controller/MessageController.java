package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    // 메시지 생성
    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(@RequestBody MessageCreateRequest request) {
        MessageResponseDto response = messageService.create(request, List.of());
        return ResponseEntity.ok(response);
    }

    // 메시지 수정
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDto> updateMessage(
            @PathVariable UUID id,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageResponseDto response = messageService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널의 메시지 목록 조회
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageResponseDto>> findAllByChannel(@PathVariable UUID channelId) {
        List<MessageResponseDto> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }
}
