package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String send(@RequestBody MessageCreateRequest messageCreateRequest) {
        Message message = messageService.create(messageCreateRequest);
        return "발송 성공\n" + message.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@RequestBody MessageUpdateRequest messageUpdateRequest) {
        Message message = messageService.update(messageUpdateRequest);
        return "수정 성공\n" + message.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable UUID id) {
        Message message = messageService.findById(id);
        messageService.delete(id);
        return "삭제 성공\n" + message.toString();
    }

    @ResponseBody
    @RequestMapping(value = {"/channelMessages/{id}"}, method = RequestMethod.GET)
    public String findAllByChannelId(@PathVariable UUID id) {
        List<Message> messages = messageService.findAllByChannelId(id);
        return messages.toString().replace("), ", ")\n\n");
    }
}
