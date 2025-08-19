package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
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

    @RequestMapping(value = "/update/{messageId}", method = RequestMethod.PUT)
    public ResponseEntity<Message> update(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId가 필요합니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }

        Message updatedMessage = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    @RequestMapping(value = "/delete/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId가 필요합니다.");
        }

        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@PathVariable UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId가 필요합니다.");
        }

        List<Message> allByChannelId = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(allByChannelId);
    }
}
