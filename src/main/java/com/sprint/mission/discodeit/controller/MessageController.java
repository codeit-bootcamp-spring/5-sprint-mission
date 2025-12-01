package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.dto.response.page.PageResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final Validator validator;

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

            Set<ConstraintViolation<MessageCreateRequest>> violations = validator.validate(request);
            if (!validator.validate(request).isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            MessageResponse response = messageService.create(request);
            URI location = URI.create("/api/messages/" + response.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .location(location)
                    .body(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "/old", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> getMessagesByChannelOld(@RequestParam UUID channelId) {
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

    @GetMapping()
    public ResponseEntity<PageResponse<MessageResponse>> getMessagesByChannel(
            @RequestParam UUID channelId,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        PageResponse<MessageResponse> response = messageService.findMessagesByChannelWithCursor(channelId, cursor, size, sort);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable UUID messageId,
            @Valid @RequestPart("messageUpdateRequest") MessageUpdateRequest request,
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
                    .body(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageDeleteResponse> deleteMessage(
            @PathVariable UUID messageId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID authorId = userDetails.getUserResponse().getId();
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