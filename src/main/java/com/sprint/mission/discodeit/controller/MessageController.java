package com.sprint.mission.discodeit.controller;

<<<<<<< HEAD
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
=======
import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @GetMapping("{messageId}")
  public ResponseEntity<MessageDto> find(@PathVariable UUID messageId) {
    log.info("[MESSAGE][FIND] id={}", messageId);
    MessageDto dto = messageService.find(messageId);
    log.debug("[MESSAGE][FIND][DONE] id={}", dto.id());
    return ResponseEntity.ok(dto);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    log.info("[MESSAGE][CREATE] channelId={}, text={}, attachments={}", messageCreateRequest.channelId(), messageCreateRequest.content(), attachments != null ? attachments.size() : 0);
    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    log.debug("[MESSAGE][CREATE][DONE] id={}", createdMessage.id());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @PatchMapping(path = "{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    log.info("[MESSAGE][UPDATE] id={}", messageId);
    MessageDto updatedMessage = messageService.update(messageId, request);
    log.debug("[MESSAGE][UPDATE][DONE] id={}", updatedMessage.id());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    log.warn("[MESSAGE][DELETE] id={}", messageId);
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "cursor", required = false) Instant cursor,
      @PageableDefault(
          size = 50,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable) {
    log.debug("[MESSAGE][LIST] channelId={}, cursor={}, pageable={}", channelId, cursor, pageable);
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
  }
}
