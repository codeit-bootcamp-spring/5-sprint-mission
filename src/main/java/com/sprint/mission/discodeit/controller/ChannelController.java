package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChannelController {

    private final ChannelService channelService;

    // request param 따라 private/public 생성이라... 필요하면 private/public api 분리
    @RequestMapping(value = "/channel", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto.DetailResponse> createChannel(@RequestBody ChannelDto.CreateRequest request) {
        return ResponseEntity.ok(channelService.create(request));
    }

    @RequestMapping(value = "/channel", method = RequestMethod.PUT)
    public ResponseEntity<ChannelDto.DetailResponse> updateChannel(@RequestBody ChannelDto.UpdateRequest request) {
        return ResponseEntity.ok(channelService.update(request));
    }

    @RequestMapping(value = "/channel/{id}", method = RequestMethod.DELETE)
        public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/channel/list/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto.DetailResponse>> findUserChannels(@PathVariable UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
