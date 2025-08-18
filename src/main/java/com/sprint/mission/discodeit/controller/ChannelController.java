package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<ChannelResponse.detail> createChannel(@Valid @ModelAttribute ChannelRequest.create dto) {
        return ResponseEntity.ok(channelService.create(dto));
    }

    @RequestMapping(value = "/create/private", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<ChannelResponse.detail> createPrivateChannel(@Valid @RequestBody ChannelRequest.createPrivate dto) {
        return ResponseEntity.ok(channelService.createPrivate(dto));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "multipart/form-data")
    public ResponseEntity<ChannelResponse.detail> updateChannel(@Valid @ModelAttribute ChannelRequest.update req) {
        return ResponseEntity.ok(channelService.update(req));
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<ChannelResponse.join> joinChannel(@ModelAttribute ChannelRequest.join dto) {
        return ResponseEntity.ok(channelService.join(dto.userId(), dto.channelId()));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> findAll() {
        return ResponseEntity.ok(channelService.findAll());
    }

    @RequestMapping(value = "/list/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse.summary>> findByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(channelService.findByUser(userId));
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String,String>> delete(@PathVariable UUID id) {
        boolean deleted = channelService.delete(id);
        return deleted
                ? ResponseEntity.ok(Map.of("message", "채널이 삭제되었습니다."))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "채널을 찾을 수 없습니다."));
    }
}
