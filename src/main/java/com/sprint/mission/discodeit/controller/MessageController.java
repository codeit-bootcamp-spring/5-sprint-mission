package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.main.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> createMessage(
            @RequestPart MessageCreateRequest messageCreateRequest,
            @RequestPart(name = "mediaContents", required = false) MultipartFile[] mediaContents
    ) throws IOException {
        try {
            List<BinaryContentCreateRequest> contentRequests = toBinaryContentRequests(mediaContents);
            Message created = messageService.create(messageCreateRequest, contentRequests);
            return ResponseEntity.status(201).body(created);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private List<BinaryContentCreateRequest> toBinaryContentRequests(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return List.of();
        }

        List<BinaryContentCreateRequest> result = new java.util.ArrayList<>(files.length);
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) {
                continue;
            }

            result.add(new BinaryContentCreateRequest(
                    f.getOriginalFilename(),
                    f.getContentType(),
                    f.getBytes()
            ));
        }

        return result;
    }

    @RequestMapping(value = "/{messageId}", method = PATCH)
    public ResponseEntity<Message> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        try {
            Message message = messageService.update(messageId, messageUpdateRequest);
            return ResponseEntity.ok(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{messageId}", method = DELETE)
    public ResponseEntity<Message> deleteMessage(
            @PathVariable UUID messageId
    ) {
        try {
            messageService.delete(messageId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/channel/{channelId}", method = GET)
    public ResponseEntity<List<Message>> getAllMessagesByChannelId(
            @PathVariable UUID channelId
    ) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = "/{messageID}", method = GET)
    public ResponseEntity<Message> getMessage(
            @PathVariable UUID messageID
    ) {
        try {
            Message message = messageService.find(messageID);
            return ResponseEntity.ok(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
