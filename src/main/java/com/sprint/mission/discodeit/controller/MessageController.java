package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> files
  ) {
    try {
      if (files != null && !files.isEmpty()) {
        List<BinaryContentCreateRequest> attachments = convertFiles(files);
        request.setAttachments(attachments);
      }
      MessageResponse response = messageService.createMessage(request);
      URI location = URI.create("/api/messages/" + response.getId());

      return ResponseEntity.status(HttpStatus.CREATED)
          .location(location)
          .body(response);
    } catch (IOException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<MessageResponse>> getMessagesByChannel(@RequestParam UUID channelId) {
    List<MessageResponse> messages = messageService.findMessagesByChannelId(channelId);
    return ResponseEntity.ok(messages);
  }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.GET)
  public ResponseEntity<MessageResponse> getMessageById(@PathVariable UUID messageId) {
    MessageResponse messageResponse = messageService.findMessage(messageId);
    if (messageResponse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      return ResponseEntity.ok(messageResponse);
    }
  }

    @GetMapping("/channels/{channelId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByChannel(
            @PathVariable UUID channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        PageResponse<MessageResponse> response = messageService.findPageMessagesByChannel(channelId, page, size);

        return ResponseEntity.ok(response.getContent());
    }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> updateMessage(
      @PathVariable UUID messageId,
      @RequestPart("messageCreateRequest") MessageUpdateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> files
  ) {
    try {
      if (files != null && !files.isEmpty()) {
        List<BinaryContentCreateRequest> attachments = convertFiles(files);
        request.setAttachmentsToAdd(attachments);
      }
      MessageResponse response = messageService.updateMessage(messageId, request);
      URI location = URI.create("/api/messages/" + response.getId());

      return ResponseEntity.status(HttpStatus.OK)
          .location(location)
          .body(response);
    } catch (IOException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<MessageDeleteResponse> deleteMessage(
      @PathVariable UUID messageId,
      @RequestParam UUID authorId) {
    MessageDeleteResponse response = messageService.deleteMessage(messageId, authorId);
    return ResponseEntity.ok(response);
  }

  private List<BinaryContentCreateRequest> convertFiles(List<MultipartFile> files) throws IOException {
    List<BinaryContentCreateRequest> attachments = new ArrayList<>();

    for (MultipartFile file : files) {
      if (!file.isEmpty()) {
        BinaryContentCreateRequest attachment = BinaryContentCreateRequest.builder()
            .fileName(file.getOriginalFilename())
            .contentType(file.getContentType())
            .size(file.getSize())
            .bytes(file.getBytes())
            .build();
        attachments.add(attachment);
      }
    }

    return attachments;
  }
}