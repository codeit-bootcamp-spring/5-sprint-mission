package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.global.api.ApiResponse;
import com.sprint.mission.discodeit.global.error.ApiException;
import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;

    // 메시지 보내기 (채널 경로 + 요청 내 채널ID 검증)
    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<ApiResponse<MessageCreateResponse>> create(
            @PathVariable UUID channelId,
            @Valid @RequestBody MessageCreateRequest request) {

        if (request.channelId() != null && !request.channelId().equals(channelId)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "경로 채널ID와 요청 채널ID가 다릅니다.");
        }

        Message created = messageService.create(request, Collections.emptyList());
        MessageCreateResponse response = new MessageCreateResponse(
                request.content(),
                request.channelId(),
                request.authorId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // 메시지 수정
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<ApiResponse<MessageUpdateResponse>> update(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request) {
        Message updated = messageService.update(messageId, request);
        MessageUpdateResponse response = new MessageUpdateResponse(
                request.newContent()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 메시지 삭제
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널 메시지 목록
    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<ApiResponse<List<Message>>> listByChannel(@PathVariable UUID channelId) {
        List<Message> list = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    // (편의) 단건 조회
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<ApiResponse<Message>> find(@PathVariable UUID messageId) {
        Message m = messageService.find(messageId);
        return ResponseEntity.ok(ApiResponse.ok(m));
    }
}
