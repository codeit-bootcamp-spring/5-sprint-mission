package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto.Detail;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @Operation(summary = "Message 생성")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto.DetailResponse> create(
      @RequestPart("messageCreateRequest") CreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.create(request.toCreate(attachments)).toDetailResponse());
  }

  @Operation(summary = "Message 수정")
  @PatchMapping("/{id}")
  public ResponseEntity<MessageDto.DetailResponse> updateMessage(@PathVariable UUID id,
      @RequestBody UpdateRequest request) {

    return ResponseEntity.ok(messageService.update(request.toUpdate(id)).toDetailResponse());
  }

  @Operation(summary = "Message 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {

    messageService.delete(id);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Message 채널 별 조회")
  @GetMapping
  public ResponseEntity<List<MessageDto.DetailResponse>> getMessagesByChannel(
      @RequestParam UUID channelId) {

    return ResponseEntity.ok(
        messageService.findAllByChannelId(channelId).stream().map(Detail::toDetailResponse)
            .toList());
  }
}
