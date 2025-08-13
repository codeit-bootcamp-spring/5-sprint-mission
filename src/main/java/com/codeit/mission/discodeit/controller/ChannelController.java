package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.service.ChannelService;
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

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(value = "/update/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<Channel> update(@PathVariable UUID channelId,
                                          @RequestBody PublicChannelUpdateRequest request) {
        Channel channel = channelService.update(channelId, request);

        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @RequestMapping(value = "/delete/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@PathVariable UUID userId) {
        List<ChannelDto> allByUserId = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
    }
}
