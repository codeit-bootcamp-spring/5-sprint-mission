package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  // 메시지 생성 (수정된 코드)
  @PostMapping
  public ResponseEntity<MessageResponseDto> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    // MultipartFile을 BinaryContentCreateRequest로 변환
    List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();
    if (attachments != null) {
      for (MultipartFile file : attachments) {
        try {
          BinaryContentCreateRequest binaryContentRequest = new BinaryContentCreateRequest(
              file.getOriginalFilename(),
              file.getContentType(),
              file.getBytes()
          );
          attachmentRequests.add(binaryContentRequest);
        } catch (IOException e) {
          throw new RuntimeException("Failed to process file", e);
        }
      }
    }

    MessageResponseDto response = messageService.create(request, attachmentRequests);
    return ResponseEntity.ok(response);
  }

  // 메시지 수정
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponseDto> updateMessage(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    MessageResponseDto response = messageService.update(messageId, request);
    return ResponseEntity.ok(response);
  }

  // 메시지 삭제
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  // 특정 채널의 메시지 목록 조회 (쿼리 파라미터 방식)
  @GetMapping
  public ResponseEntity<List<MessageResponseDto>> findAllByChannel(@RequestParam UUID channelId) {
    List<MessageResponseDto> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok(messages);
  }
}
