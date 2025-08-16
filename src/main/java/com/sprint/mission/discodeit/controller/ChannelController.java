package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.main.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = POST)
    public ResponseEntity<Channel> createChannel(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
    ) {
        Channel channel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.ok(channel);
    }

    @RequestMapping(value = "/private", method = POST)
    public ResponseEntity<Channel> createChannel(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
    ) {
        Channel channel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.ok(channel);
    }

    @RequestMapping(value = "/{channelId}", method = PATCH)
    public ResponseEntity<Channel> updateChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
    ) {
        try {
            Channel channel = channelService.update(channelId, publicChannelUpdateRequest);
            return ResponseEntity.ok(channel);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{channelId}", method = DELETE)
    public ResponseEntity<Channel> deleteChannel(
            @PathVariable UUID channelId
    ) {
        try {
            channelService.delete(channelId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/user/{userId}", method = GET)
    public ResponseEntity<List<ChannelDto>> getAllChannelsByUserId(
            @PathVariable UUID userId
    ) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    @RequestMapping(value = "/{channelId}", method = GET)
    public ResponseEntity<ChannelDto> getChannel(
            @PathVariable UUID channelId
    ) {
        try {
            ChannelDto channelDto = channelService.find(channelId);
            return ResponseEntity.ok(channelDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
