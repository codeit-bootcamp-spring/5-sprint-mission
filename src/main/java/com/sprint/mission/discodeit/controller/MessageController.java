package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
@Tag(name = "Message", description = "Message API")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @RequestPart MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments)
            .map(files -> files.stream()
                .map(file -> {
                  try {
                    return new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                    );
                  } catch (IOException e) {
                    throw new ThrowableIOException("메세지 생성 중 파일 불러오기 실패", e);
                  }
                }).toList())
            .orElse(List.of());

    Message message = messageService.create(messageCreateRequest, binaryContentCreateRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @Operation(summary = "Message 내용 수정")
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest) {
    Message message = messageService.update(messageId, messageUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @Operation(summary = "Message 삭제")
  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Message가 성공적으로 삭제됨");
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @GetMapping
  public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }
}
