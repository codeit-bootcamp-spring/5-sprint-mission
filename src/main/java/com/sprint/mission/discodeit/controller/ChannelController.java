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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(path="/createPublic", method= RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(200).body(channel);
    }

    @RequestMapping(path="/createPrivate", method= RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(200).body(channel);
    }

    @RequestMapping(path="/{id}/update", method=RequestMethod.POST)
    public ResponseEntity<Channel> updateChannel(@PathVariable UUID id,
                                                 @RequestBody PublicChannelUpdateRequest request) {
        Channel channel = channelService.update(id, request);
        return ResponseEntity.status(201).body(channel);
    }

    @RequestMapping(path="/{id}/delete", method=RequestMethod.DELETE)
    public ResponseEntity<ChannelDto> deleteChannel(@PathVariable UUID id) {
        Optional<ChannelDto> channel = Optional.ofNullable(channelService.find(id));
        channel.orElseThrow(() -> new NoSuchElementException("id가 {" + id + "}인 채널이 존재하지 않습니다."));
        channelService.delete(id);
        return ResponseEntity.ok(channel.get());
    }

    @RequestMapping(path="/{id}/findAll", method=RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@PathVariable UUID id) {
        List<ChannelDto> channels = channelService.findAllByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }

}
