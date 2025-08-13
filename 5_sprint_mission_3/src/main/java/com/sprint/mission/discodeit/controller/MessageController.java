package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
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

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public Message create(@RequestBody MessageCreateRequest messageCreateRequest) {
        return messageService.create(messageCreateRequest);
    }

    @RequestMapping(path = "/channel/{channelId}", method = RequestMethod.GET)
    public List<Message> findAllByChannel(@PathVariable("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    public Message update(@PathVariable("messageId") UUID messageId, @RequestBody MessageUpdateRequest messageUpdateRequest) {
        return messageService.update(messageId, messageUpdateRequest);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }

}
