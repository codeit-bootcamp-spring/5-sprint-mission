package com.sprint.mission.discodeit.presentation.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.presentation.api.ChannelApi;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    @RequestMapping(method = POST)
    public ResponseEntity<ChannelResponse> create(@RequestBody CreateChannelRequest request) {
        ChannelResponse created = channelService.createChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<ChannelResponse> find(@PathVariable("id") UUID id) {
        return channelService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Channel with id " + id + " not found"));
    }

    @RequestMapping(value = "/visible/{userId}", method = GET)
    public ResponseEntity<List<ChannelResponse>> findVisible(@PathVariable("userId") UUID userId) {
        List<ChannelResponse> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    @RequestMapping(method = PUT)
    public ResponseEntity<ChannelResponse> update(@RequestBody UpdateChannelRequest request) {
        ChannelResponse updated = channelService.update(request);
        if (updated == null) throw new IllegalArgumentException("수정할 수 없거나 존재하지 않는 채널입니다");
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity<ChannelResponse> delete(@PathVariable("id") UUID id) {
        if (!channelService.removeById(id)) throw new NotFoundException("Channel with id " + id + " not found");
        return ResponseEntity.noContent().build();
    }
}
