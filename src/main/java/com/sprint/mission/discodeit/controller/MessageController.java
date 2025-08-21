package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MessageController {

  private final MessageService messageService;

  // 생성자 주입
  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  // ✅메시지 생성
  @PostMapping(value = "/api/messages", consumes = "multipart/form-data")
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) { //여러 파일 첨부 가능
    Message created = messageService.create(request, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }


  // ✅메시지 단건 조회
  @GetMapping("/api/messages/{messageId}")
  public ResponseEntity<Message> findById(@PathVariable UUID messageId) {
    Message found = messageService.findById(messageId);
    return ResponseEntity.ok(found);
  }

  // ✅채널 내 모든 메시지 조회
  @GetMapping("/api/messages")
  public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok(messages);
  }

  // ✅메시지 수정
  @PatchMapping("/api/messages/{messageId}")
  public ResponseEntity<Message> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    Message updated = messageService.update(messageId, request);
    return ResponseEntity.ok(updated);
  }


  // ✅메시지 삭제
  @DeleteMapping("/api/messages/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

}
