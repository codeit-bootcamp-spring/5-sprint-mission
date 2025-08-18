package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<Message> createMessage(
            @RequestPart MessageCreateRequest messageCreateRequest,
            @RequestPart(required = false) MultipartFile[] attachments) throws IOException {

        List<BinaryContentCreateRequest> profileCreateRequestList = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                BinaryContentCreateRequest profileCreateRequest = new BinaryContentCreateRequest(
                        attachment.getOriginalFilename(),
                        attachment.getContentType(),
                        attachment.getBytes()
                );
                profileCreateRequestList.add(profileCreateRequest);
            }
        }

        Message message = messageService.create(messageCreateRequest, profileCreateRequestList);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(path = "/update/{messageId}", method = RequestMethod.PUT)
    public ResponseEntity<Message> updateMessage(
            @PathVariable UUID messageId,
            @RequestPart MessageUpdateRequest messageUpdateRequest) {

        Message message = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @RequestMapping(value = "/delete/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/findAllByChannelId/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAll(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
