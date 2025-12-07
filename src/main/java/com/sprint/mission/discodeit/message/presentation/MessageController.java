package com.sprint.mission.discodeit.message.presentation;

import com.sprint.mission.discodeit.common.presentation.dto.PaginationRequest;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationResponse;
import com.sprint.mission.discodeit.message.application.MessageService;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.MessageDto;
import com.sprint.mission.discodeit.message.presentation.dto.MessageUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageControllerDocs {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto create(
        @RequestPart("messageCreateRequest") @Valid MessageCreateRequest request,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        return messageService.create(request, attachments);
    }

    @GetMapping
    public PaginationResponse<MessageDto> findAllByChannelId(
        @RequestParam UUID channelId,
        @RequestParam(required = false) Instant cursor,
        @Valid PaginationRequest paginationRequest
    ) {
        return messageService.findAllByChannelId(channelId, cursor, paginationRequest);
    }

    @PatchMapping("/{messageId}")
    public MessageDto update(
        @PathVariable UUID messageId,
        @RequestBody @Valid MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID messageId) {
        messageService.deleteById(messageId);
    }
}
