package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.AddMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;


    @RequestMapping(method= RequestMethod.POST)
    public Message sendMessage(
            @RequestBody AddMessageRequest addMessageRequest
    ){
        return messageService.addMessage(addMessageRequest);
    }

    @RequestMapping(path="/{messageId}", method= RequestMethod.POST)
    public Message updateMessage(
            @PathVariable UUID messageId,
            @RequestBody UpdateMessageRequest addMessageRequest
    ){
        return messageService.updateMessage(messageId, addMessageRequest);
    }

    @RequestMapping(path="/{messageId}", method= RequestMethod.DELETE)
    public void deleteMessage(
            @PathVariable UUID messageId
    ){
        messageService.deleteMessage(messageId);
    }

    @RequestMapping(path="/{channelId}", method = RequestMethod.GET)
    public List<Message> getAllMessageByChannelId(
            @PathVariable UUID channelId
    ){
        return messageService.getAllByChannelId(channelId);
    }

}
