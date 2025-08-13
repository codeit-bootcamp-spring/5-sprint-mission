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
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/message")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(path="/create", method= RequestMethod.POST, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> createMessage(
            @RequestPart MessageCreateRequest messageRequest,
            @RequestPart List<MultipartFile> files) throws IOException {
        List<BinaryContentCreateRequest> filesRequests = new ArrayList<>();
        for(MultipartFile multipartFile : files){
            if(!multipartFile.isEmpty()) {
                filesRequests.add(new  BinaryContentCreateRequest(
                        multipartFile.getOriginalFilename(),
                        multipartFile.getContentType(),
                        multipartFile.getBytes()
                ));
            }
        }
        Message message = messageService.create(messageRequest,filesRequests);
        return ResponseEntity.status(200).body(message);
    }

    @RequestMapping(path="/{id}/update", method=RequestMethod.POST)
    public ResponseEntity<Message> updateMessage(@PathVariable UUID id, @RequestBody MessageUpdateRequest request) {
        Message message = messageService.update(id, request);
        return ResponseEntity.status(201).body(message);
    }

    @RequestMapping(path="/{id}/delete", method=RequestMethod.DELETE)
    public ResponseEntity<Message> deleteMessage(@PathVariable UUID id) {
        Optional<Message> message = Optional.ofNullable(messageService.find(id));
        message.orElseThrow(() -> new NoSuchElementException("id가 {" + id + "}인 메시지가 존재하지 않습니다."));
        messageService.delete(id);
        return ResponseEntity.ok(message.get());
    }

    @RequestMapping(path="/{channelId}/findAll", method=RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllMessageByChannelId(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
