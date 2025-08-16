package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(path = "/createPublic", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest channelCreateRequest) {
        Channel channel = channelService.create(channelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(path = "/createPrivate", method = RequestMethod.POST)
    private ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest channelCreateRequest) {
        Channel channel = channelService.create(channelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(path = "/update/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<Channel> updateChannel(
            @PathVariable UUID channelId,
            @RequestPart PublicChannelUpdateRequest channelUpdateRequest) {

        Channel channel = channelService.update(channelId, channelUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @RequestMapping(value = "/delete/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/findAllByUserId/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@PathVariable UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
