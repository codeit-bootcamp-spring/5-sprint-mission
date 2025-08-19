package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    //    [ ] 메시지를 보낼 수 있다.
    @RequestMapping(path = "create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> createMessage(
            @RequestPart MessageCreateRequest messageCreateRequest,
            @RequestPart(required = false) List<BinaryContentCreateRequest> binaryContentCreateRequests
    ) {


        Message message = messageService.create(messageCreateRequest, binaryContentCreateRequests);
        return ResponseEntity.status(201).body(message);
    }


    //            [ ] 메시지를 수정할 수 있다.
    @RequestMapping(path = "update/{messageId}", method = RequestMethod.PUT)
    public ResponseEntity<Message> updateMessage(
            @RequestBody MessageUpdateRequest request,
            @PathVariable UUID messageId
    ) {
        Message message = messageService.update(messageId, request);
        return ResponseEntity.status(201).body(message);
    }

    //            [ ] 메시지를 삭제할 수 있다.
    @RequestMapping(path = "delete/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(204).build();
    }

    //            [ ] 특정 채널의 메시지 목록을 조회할 수 있다.
    @RequestMapping(path = "find/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findByChannelId(@PathVariable UUID channelId) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(200).body(messageList);
    }
}
