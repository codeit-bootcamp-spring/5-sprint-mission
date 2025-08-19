package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> create(@RequestPart MessageCreateRequest messageCreateRequest,
        @RequestPart(required = false) MultipartFile[] attachments) throws IOException {
        if (!StringUtils.hasText(messageCreateRequest.content())) {
            throw new IllegalArgumentException("content가 필요합니다.");
        }
        if (messageCreateRequest.channelId() == null) {
            throw new IllegalArgumentException("channelId가 필요합니다.");
        }
        if (messageCreateRequest.authorId() == null) {
            throw new IllegalArgumentException("authorId가 필요합니다.");
        }

        List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();

        if (attachments != null && attachments.length > 0) {
            for (MultipartFile attachment : attachments) {
                if (attachment != null && !attachment.isEmpty()) {
                    BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest(
                        attachment.getOriginalFilename(),
                        attachment.getContentType(),
                        attachment.getBytes()
                    );
                    attachmentRequests.add(attachmentRequest);
                }
            }
        }

        Message message = messageService.create(messageCreateRequest, attachmentRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> update(@PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId가 필요합니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }

        Message updatedMessage = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId가 필요합니다.");
        }

        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(
        @RequestParam("channelId") UUID channelId) {
        List<Message> allByChannelId = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(allByChannelId);
    }
}
