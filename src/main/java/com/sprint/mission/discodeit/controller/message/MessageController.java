package com.sprint.mission.discodeit.controller.message;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentService;
import com.sprint.mission.discodeit.service.message.MessageService;
import com.sprint.mission.discodeit.support.FileNames;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class MessageController {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  @GetMapping({"", "/"})
  @ResponseStatus(HttpStatus.OK)
  public List<MessageResponse> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
    return messageService.findAllByChannelId(channelId);
  }

  @PostMapping(path = {"", "/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public MessageResponse create(
      @RequestPart("messageCreateRequest") @Valid MessageCreateRequest req,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException {
    Set<UUID> attachmentIds = new LinkedHashSet<>();
    for (MultipartFile attachment : attachments) {
      String ct = FileNames.normalizeContentType(attachment.getContentType());
      String original = attachment.getOriginalFilename();
      String fileName = FileNames.buildStoredName(original, ct);

      BinaryContentResponse saved = binaryContentService.create(
          new BinaryContentCreateRequest(fileName, ct, attachment.getBytes())
      );
      attachmentIds.add(saved.id());
    }
    return messageService.create(req, attachmentIds);
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<Void> update(@PathVariable("id") UUID id,
      @Valid @RequestBody MessageUpdateRequest body) {
    messageService.update(id, body);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id,
      @RequestParam("actorId") UUID actorId) {
    messageService.delete(id, actorId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<MessageResponse> find(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(messageService.find(id));
  }
}
