package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/message")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart MessageCreateRequest messageCreateRequest,
            @RequestPart(required = false) List<MultipartFile> profiles
    ) throws IOException {
        List<BinaryContentCreateRequest> binaryContents = new ArrayList<>();

        if(!profiles.isEmpty()) {
            for (MultipartFile profile : profiles) {
                binaryContents.add(new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getSize(),
                        profile.getBytes()
                ));
            }
        }

        Message message = messageService.create(messageCreateRequest, binaryContents);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(path = "update", method = RequestMethod.PATCH)
    public ResponseEntity<Message> update(
        @RequestParam("messageId") UUID messageId,
        @RequestBody MessageUpdateRequest request){
        Message message = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @RequestMapping(path = "delete", method = RequestMethod.DELETE)
    public ResponseEntity<Message> delete(@RequestParam("messageId") UUID messageId){
            messageService.delete(messageId);
            return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "findAll/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAll(@PathVariable("channelId") UUID channelId){
            List<Message> findByChannelId = messageService.findAllByChannelId(channelId);
            return ResponseEntity.ok().body(findByChannelId);
    }


}
