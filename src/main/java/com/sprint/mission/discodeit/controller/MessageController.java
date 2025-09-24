package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "Message", description = "메세지 관리 API")
@RestController
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  //메시지 생성
  @Operation(summary = "메세지 생성")
  @PostMapping(value = "/api/messages", consumes = "multipart/form-data")
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageDto") MessageDto dto,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    MessageDto created = messageService.create(dto, attachments);
    log.info("메시지 생성 완료: messageId={}", created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }


  //메시지 단건 조회
  @Operation(summary = "메시지 단건 조회")
  @GetMapping("/api/messages/{messageId}")
  public ResponseEntity<MessageDto> findById(@PathVariable("messageId") UUID messageId) {
    log.info("메시지 단건 조회 요청: messageId={}", messageId);
    MessageDto found = messageService.findById(messageId);
    return ResponseEntity.ok(found);
  }


  //채널 내 모든 메시지 조회 -> 비효율적 수정해야함!
  @Operation(summary = "채널 내 모든 메시지 조회")
  @GetMapping("/api/messages")
  public ResponseEntity<List<MessageDto>> findAllByChannelId(@RequestParam UUID channelId) {
    log.info("채널 내 메시지 전체 조회 요청: channelId={}", channelId);
    List<MessageDto> dtoList = messageService.findAllByChannelId(
        channelId);
    return ResponseEntity.ok(dtoList);
  }


  //메시지 수정
  @Operation(summary = "메시지 수정")
  @PatchMapping("/api/messages/{messageId}")
  public ResponseEntity<MessageDto> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageDto dto
  ) {
    MessageDto updated = messageService.update(messageId, dto);
    log.info("메시지 수정 완료: messageId={}", messageId);
    return ResponseEntity.ok(updated);
  }


  //메시지 삭제
  @Operation(summary = "메시지 삭제")
  @DeleteMapping("/api/messages/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    log.info("메시지 삭제 완료: messageId={}", messageId);
    return ResponseEntity.noContent().build();
  }
}