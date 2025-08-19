package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

  // TODO 나중에 로그인 중인 사용자만 처리하면 될듯?
  private final MessageService messageService;

  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<MessageDto.DetailResponse> create(
      @RequestPart("messageCreateRequest") MessageDto.MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    // 미션 FE에 맞추기용...
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(messageService.create(MessageDto.CreateRequest.builder()
            .content(messageCreateRequest.getContent())
            .authorId(messageCreateRequest.getAuthorId())
            .channelId(messageCreateRequest.getChannelId())
            .attachments(attachments)
            .build()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<MessageDto.DetailResponse> updateMessage(@PathVariable UUID id,
      @RequestBody MessageDto.UpdateRequest request) {
    request.setId(id);
    return ResponseEntity.ok(messageService.update(request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
    messageService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<MessageDto.DetailResponse>> getMessagesByChannel(
      @RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }
}
