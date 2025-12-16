package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDTO;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "메시지 관련 API")
@Slf4j
public class MessageController {

  private final MessageService messageService;

  // [ ] 메시지 전송
  @Operation(summary = "메시지 전송 API", responses = {
      @ApiResponse(responseCode = "201", description = "메시지가 정상적으로 전송되었습니다."),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 값입니다."),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @Timed("message.create.async")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDTO> sendMessage(
      @RequestPart @Valid MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> attachments) throws IOException {
    log.info("메시지 전송 요청 수신 message={}", messageCreateRequest.content());
    MessageDTO message = messageService.createMessage(messageCreateRequest, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(message); // 201 Created
  }

  // [ ] 메시지 수정
  @Operation(summary = "메시지 수정 API", responses = {
      @ApiResponse(responseCode = "200", description = "메시지가 정상적으로 수정되었습니다."),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 값입니다."),
      @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없습니다."),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDTO> modifyMessage(
      @PathVariable UUID messageId,
      @RequestBody @Valid MessageUpdateRequest messageUpdateRequest) {
    log.info("메시지 수정 요청 수신");
    MessageDTO message = messageService.updateMessage(messageId, messageUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(message); // 200 OK
  }

  // [ ] 메시지 삭제
  @Operation(summary = "메시지 삭제 API", responses = {
      @ApiResponse(responseCode = "204", description = "메시지가 정상적으로 삭제되었습니다."),
      @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없습니다."),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
    log.info("메시지 삭제 요청 수신");
    messageService.deleteMessage(messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
  }

  // [ ] 특정 채널의 메시지 조회
  @Operation(summary = "채널별 메시지 조회 API", responses = {
      @ApiResponse(responseCode = "200", description = "메시지가 정상적으로 조회되었습니다."
          , content = @Content(schema = @Schema(implementation = PageResponse.class))),
      @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없습니다."),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @GetMapping
  public ResponseEntity<PageResponse<MessageDTO>> findMessageByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(
          // size = 10, page = 0 default
          sort = "createdAt",
          direction = Direction.ASC
      ) Pageable pageable) {
    PageResponse<MessageDTO> messages = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity.status(HttpStatus.OK).body(messages); // 200 OK
  }

  // 테스트용 컨트롤러
  @GetMapping("/cursor")
  public ResponseEntity<PageResponse<MessageDTO>> findMessageByChannelIdCursor(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(
          // size = 10, page = 0 default
          sort = "createdAt",
          direction = Direction.ASC
      ) Pageable pageable) {
    PageResponse<MessageDTO> messages = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity.status(HttpStatus.OK).body(messages); // 200 OK
  }

  @GetMapping("/page")
  public ResponseEntity<PageResponse<MessageDTO>> findMessageByChannelIdPage(
      @RequestParam UUID channelId,
      @PageableDefault(
          // size = 10, page = 0 default
          sort = "createdAt",
          direction = Direction.ASC
      ) Pageable pageable) {
    PageResponse<MessageDTO> messages = messageService.findAllByChannelId(channelId, null,
        pageable);
    return ResponseEntity.status(HttpStatus.OK).body(messages); // 200 OK
  }
}
