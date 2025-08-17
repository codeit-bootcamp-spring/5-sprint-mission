package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/public",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest req) {
        if (req == null || req.name() == null) {
            throw new IllegalArgumentException("name is empty");
        }
        Channel channel = channelService.create(req);
        return ResponseEntity.created(URI.create("/api/channels/" + channel.getId())).body(channel);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{channelId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelDto> find(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.find(channelId));
    }

    @RequestMapping(
            method = RequestMethod.PUT, value = "/{channelId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Channel> update(@PathVariable UUID channelId,
                                          @RequestBody PublicChannelUpdateRequest req) {
        if (req == null) throw new IllegalArgumentException("Empty");
        Channel updated = channelService.update(channelId, req);
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{channelId}"
    )
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/visible",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ChannelDto>> findVisible(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}