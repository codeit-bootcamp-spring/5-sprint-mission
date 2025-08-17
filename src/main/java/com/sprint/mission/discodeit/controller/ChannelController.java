package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final MessageService messageService;

    public ChannelController(
            @Qualifier("basicChannelService") ChannelService channelService,
            @Qualifier("basicMessageService") MessageService messageService
    ) {
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublicChannel(@RequestBody ChannelPublicCreateRequest request) {
        ChannelResponse response = channelService.createPublicChannel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPrivateChannel(@RequestBody ChannelPrivateCreateRequest request) {
        ChannelResponse response = channelService.createPrivateChannel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request) {
        if (request.getId() == null || !channelId.equals(request.getId())) {
            throw new IllegalArgumentException("Channel ID in path does not match ID in request body.");
        }
        ChannelResponse response = channelService.update(request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> getMessagesInChannel(@PathVariable UUID channelId) {
        List<MessageResponse> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }
}
