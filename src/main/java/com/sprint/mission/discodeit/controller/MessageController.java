package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// * [ ] 메시지를 보낼 수 있다.
// * [ ] 메시지를 수정할 수 있다.
// * [ ] 메시지를 삭제할 수 있다.
// * [ ] 특정 채널의 메시지 목록을 조회할 수 있다.

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
        return ResponseEntity.status(201).body(message);
    }

    @RequestMapping(path = "update", method = RequestMethod.POST)
    public ResponseEntity<Message> update(
        @RequestParam UUID messageId,
        @RequestBody MessageUpdateRequest messageUpdateRequest){
        messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.status(204).build();
    }

    @RequestMapping(path = "delete", method = RequestMethod.DELETE)
    public ResponseEntity<Message> delete(UUID messageId){
            messageService.delete(messageId);
            return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "findAll/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAll(@PathVariable("id") UUID channelId){
            List<Message> findByChannelId = messageService.findAllByChannelId(channelId);
            return ResponseEntity.ok().body(findByChannelId);
    }


}
