package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(path = "/message", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> sendMessage(
            @RequestPart("messageCreateRequest")MessageCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
            ) throws IOException {

        List<BinaryContentCreateRequest> binaryRequest = Collections.emptyList();
        if (files != null && !files.isEmpty()) {
            binaryRequest = files.stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .map(f ->{
                        try{
                            return new BinaryContentCreateRequest(
                                    f.getOriginalFilename(),
                                    f.getContentType(),
                                    f.getBytes()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
        }
        Message created = messageService.create(request,binaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @RequestMapping(path = "/message/{messageId}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> updateMessage(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request) {
        return  ResponseEntity.status(HttpStatus.OK).body(messageService.update(messageId, request));
    }

    @RequestMapping(path = "/message/{messagesId}")
    public ResponseEntity<List<Message>> deleteMessage(@PathVariable UUID messagesId) {
        messageService.delete(messagesId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/channels/{channelId}/messages")
    public ResponseEntity<List<Message>> listMessageByChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
