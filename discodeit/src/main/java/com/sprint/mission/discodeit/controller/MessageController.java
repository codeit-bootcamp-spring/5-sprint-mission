package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(path="create",method= RequestMethod.POST)
    public ResponseEntity<Message> create(
                                          @RequestPart MessageCreateRequest messageCreateRequest,
                                          @RequestPart List<BinaryContentCreateRequest> binaryContentCreateRequests
    ){

        Message message = messageService.create(messageCreateRequest,binaryContentCreateRequests);
        return ResponseEntity.status(201).body(message);
    }

    @RequestMapping(path="update",method= RequestMethod.POST)
    public ResponseEntity<Message> update(
                                 @RequestParam("messageId")UUID messageId,
                                 @RequestPart MessageUpdateRequest request
    ){
        Message message=messageService.update(messageId,request);
        return ResponseEntity.status(201).body(message);

    }


    @RequestMapping(path="delete",method=RequestMethod.DELETE)
    public ResponseEntity<Message> delete(@RequestParam("messageId")UUID messageId){
        messageService.delete(messageId);
        return ResponseEntity.status(200).body(null);
    }

    @RequestMapping(path="find",method=RequestMethod.GET)
    public ResponseEntity<List<Message>> findMessageByChannelId(@RequestParam("channelId") UUID channelId){
        List<Message> messages=messageService.findAllByChannelId(channelId);
        return  ResponseEntity.status(200).body(messages);
    }






}
