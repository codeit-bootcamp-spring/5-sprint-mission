package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @Override
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> create (@RequestBody PublicChannelCreateRequest request) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @Override
    @PostMapping( "/private")
    public ResponseEntity<ChannelDto> create (@RequestBody PrivateChannelCreateRequest request) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @Override
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update (
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest request) {
        ChannelDto udpatedChannel = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(udpatedChannel);
    }

    @Override
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete (@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll (@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
