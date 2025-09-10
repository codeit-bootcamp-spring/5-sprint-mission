package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    @PostMapping("public")
    public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PostMapping("private")
    public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PatchMapping("{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID id, @RequestBody PublicChannelUpdateRequest request) {
        ChannelDto updatedChannel = channelService.update(id, request);
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }
}
