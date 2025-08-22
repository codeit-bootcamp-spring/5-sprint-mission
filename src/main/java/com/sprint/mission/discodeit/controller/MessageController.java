package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Message")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "채널 메시지 목록 조회")
  @GetMapping
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok(messages);
  }

  @Operation(summary = "메시지 생성 (multipart)")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = toBinaryCreateRequests(attachments);
    Message created = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity
        .created(URI.create("/api/messages/" + created.getId()))
        .body(created);
  }

  @Operation(summary = "메시지 내용 수정")
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> update(@PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request) {
    Message updated = messageService.update(messageId, request);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "메시지 삭제")
  @DeleteMapping("/{messageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
  }

  private List<BinaryContentCreateRequest> toBinaryCreateRequests(List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return Collections.emptyList();
    }
    return attachments.stream().map(file -> {
      try {
        return new BinaryContentCreateRequest(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getBytes()
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());
  }
}
