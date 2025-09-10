package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream().map(file -> {
          try {
            return new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
            );
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }).toList())
        .orElse(new ArrayList<>());

    Message created = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(messageMapper.toDto(created));
  }

  @Override
  @PatchMapping(path = "{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MessageDto> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    Message updated = messageService.update(messageId, request);
    return ResponseEntity.ok(messageMapper.toDto(updated));
  }

  @Override
  @DeleteMapping("{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "50") int size
  ) {
    Slice<Message> slice = messageService.findAllByChannelId(channelId, page, size);
    var dtoSlice = slice.map(messageMapper::toDto);
    var body = pageResponseMapper.fromSlice(dtoSlice);
    return ResponseEntity.ok(body);
  }
}
