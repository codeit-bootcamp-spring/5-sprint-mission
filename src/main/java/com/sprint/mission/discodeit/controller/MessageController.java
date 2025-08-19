package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

  private final BasicMessageService messageService;

  @RequestMapping(value = "/send",
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> send(
      @RequestPart MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> multipartFiles
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();
    if (multipartFiles != null && !multipartFiles.isEmpty()) {
      binaryContentCreateRequests = multipartFiles.stream()
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
          })
          .toList();
    }
    Message message = messageService.create(messageCreateRequest,
        List.copyOf(binaryContentCreateRequests));
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public ResponseEntity<Message> update(@RequestPart MessageUpdateRequest messageUpdateRequest) {
    Message message = messageService.update(messageUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Message> delete(@PathVariable UUID id) {
    messageService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(value = {"/channelMessages/{id}"}, method = RequestMethod.GET)
  public ResponseEntity<List<Message>> findAllByChannelId(@PathVariable UUID id) {
    List<Message> messages = messageService.findAllByChannelId(id);
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }
}
