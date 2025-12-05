package com.sprint.mission.discodeit.api.controller;

import com.sprint.mission.discodeit.api.docs.MessageControllerDocs;
import com.sprint.mission.discodeit.api.dto.request.Pageable;
import com.sprint.mission.discodeit.api.dto.response.PageResponse;
import com.sprint.mission.discodeit.domain.message.dto.data.MessageDto;
import com.sprint.mission.discodeit.domain.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.domain.message.service.MessageService;
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
    public PageResponse<MessageDto> findAllByChannelId(
        @RequestParam UUID channelId,
        @RequestParam(required = false) Instant cursor,
        @Valid Pageable pageable
    ) {
        return messageService.findAllByChannelId(channelId, cursor, pageable);
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
