package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @RequestPart MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> profiles
  ) throws IOException {
    List<BinaryContentCreateRequest> binaryContents = new ArrayList<>();

    if (!profiles.isEmpty()) {
      for (MultipartFile profile : profiles) {
        binaryContents.add(new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        ));
      }
    }

    Message message = messageService.create(messageCreateRequest, binaryContents);
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }


  @PatchMapping(path = "/{messageId}")
  @Operation(summary = "Message 내용 수정", description = "수정할 Message ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
  })
  public ResponseEntity<Message> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    Message message = messageService.update(messageId, request);
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @DeleteMapping(path = "/{messageId}")
  @Operation(summary = "Message 삭제", description = "삭제할 Message ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
  })
  public ResponseEntity<Message> delete(@RequestParam("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  })
  @GetMapping
  public ResponseEntity<List<Message>> findAll(@RequestParam("channelId") UUID channelId) {
    List<Message> findByChannelId = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok().body(findByChannelId);
  }


}
