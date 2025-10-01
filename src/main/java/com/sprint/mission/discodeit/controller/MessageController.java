package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
          @Validated @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
          @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.info("메시지 생성 요청 수신: channelId={}, authorId={}, content={}",
            messageCreateRequest.channelId(), messageCreateRequest.authorId(), messageCreateRequest.content());

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
                        log.error("첨부파일 변환 실패: fileName={}", file.getOriginalFilename(), e);
                        throw new RuntimeException(e);
                      }
                    })
                    .toList())
            .orElse(new ArrayList<>());

    log.debug("첨부파일 개수: {}", attachmentRequests.size());

    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

    log.info("메시지 생성 완료: messageId={}", createdMessage.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @PatchMapping(path = "{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
                                           @Validated @RequestBody MessageUpdateRequest request) {
    log.info("메시지 수정 요청 수신: messageId={}, newContent={}", messageId, request.newContent());

    MessageDto updatedMessage = messageService.update(messageId, request);

    log.info("메시지 수정 완료: messageId={}", updatedMessage.id());
    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    log.info("메시지 삭제 요청 수신: messageId={}", messageId);

    messageService.delete(messageId);

    log.info("메시지 삭제 완료: messageId={}", messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
    log.info("채널별 메시지 조회 요청 수신: channelId={}, cursor={}, pageSize={}",
            channelId, cursor, pageable.getPageSize());

    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor, pageable);

    log.info("채널별 메시지 조회 완료: channelId={}, 반환 메시지 수={}", channelId, messages.content().size());
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }
}