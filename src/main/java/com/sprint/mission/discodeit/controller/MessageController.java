package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.CursorPageResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{channelId}/messages")
public class MessageController {

    private final MessageService messageService;
    private final PageResponseMapper pageResponseMapper;
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    // 메시지 생성
    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(
        @PathVariable UUID channelId,
        @RequestBody MessageCreateRequest request
    ) {
        MessageResponseDto response = messageService.create(request.withChannelId(channelId), List.of());
        return ResponseEntity.ok(response);
    }

    // 메시지 수정
    @PutMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(
        @PathVariable UUID channelId,
        @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request
    ) {
        MessageResponseDto response = messageService.update(messageId, request);
        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
        @PathVariable UUID channelId,
        @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // 채널 전체 메시지 조회
    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> findAllByChannel(@PathVariable UUID channelId) {
        List<MessageResponseDto> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    // 오프셋 페이지네이션
    @GetMapping("/paging")
    public PageResponse<MessageResponseDto> findByChannelWithPaging(
        @PathVariable UUID channelId,
        @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Slice<Message> slice = messageRepository.findByChannelId(channelId, pageable);
        Slice<MessageResponseDto> dtoSlice = slice.map(messageMapper::toDto);
        return pageResponseMapper.fromSlice(dtoSlice);
    }

    // 커서 페이지네이션
    @GetMapping("/cursor")
    public ResponseEntity<CursorPageResponse<MessageResponseDto>> findByCursor(
        @PathVariable UUID channelId,
        @RequestParam(required = false) Instant cursor,
        @RequestParam(defaultValue = "50") int size
    ) {
        CursorPageResponse<MessageResponseDto> response = messageService.findByCursor(channelId, cursor, size);
        return ResponseEntity.ok(response);
    }
}

