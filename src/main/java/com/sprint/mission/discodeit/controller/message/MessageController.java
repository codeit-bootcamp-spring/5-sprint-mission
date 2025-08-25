package com.sprint.mission.discodeit.controller.message;

import static com.sprint.mission.discodeit.support.Constants.MAX_MESSAGE_ATTACHMENTS;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.message.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class MessageController {

  private final MessageService messageService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<MessageDto> findAllByChannelId(

      @RequestParam("channelId")
      UUID channelId
  ) {

    return messageService.findAllByChannelId(channelId);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public MessageDto create(

      @RequestPart("messageCreateRequest")
      @Valid
      MessageCreateRequest req,

      @RequestPart(value = "attachments", required = false)
      @Size(max = MAX_MESSAGE_ATTACHMENTS)
      List<@NotNull MultipartFile> attachments
  ) throws IOException {

    return messageService.create(req, attachments);
  }

  @DeleteMapping(path = "/{messageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(

      @PathVariable("messageId")
      UUID messageId
  ) {
    messageService.delete(messageId);
  }

  @PatchMapping(path = "/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public MessageDto update(

      @PathVariable("messageId")
      UUID messageId,

      @RequestBody
      @Valid
      MessageUpdateRequest req
  ) {

    return messageService.update(messageId, req);
  }
}
