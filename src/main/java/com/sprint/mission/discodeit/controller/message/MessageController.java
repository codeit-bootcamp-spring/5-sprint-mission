package com.sprint.mission.discodeit.controller.message;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentService;
import com.sprint.mission.discodeit.service.message.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Validated
public class MessageController {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<MessageResponse> findAllByChannelId(

      @RequestParam("channelId")
      @NotNull
      UUID channelId
  ) {

    return messageService.findAllByChannelId(channelId);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public MessageResponse create(

      @RequestPart("messageCreateRequest")
      @Valid
      MessageCreateRequest req,

      @RequestPart(value = "attachments", required = false)
      List<MultipartFile> attachments
  ) throws IOException {

    return messageService.create(req, attachments);
  }

  @PutMapping(path = "/{messageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public MessageResponse update(
      @PathVariable("messageId")
      UUID messageId,

      @RequestBody
      @Valid
      MessageUpdateRequest req
  ) {

    return messageService.update(messageId, req);
  }

  @DeleteMapping(path = "/{messageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable("messageId")
      UUID messageId
  ) {
    messageService.delete(messageId);
  }
}
