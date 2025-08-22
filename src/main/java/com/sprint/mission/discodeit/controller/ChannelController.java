package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Channel", description = "Channel API")
@RequestMapping("api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelResponse.Detail> createChannel(
            @Valid @RequestBody ChannelRequest.Create dto) {
        Channel channel = channelService.create(dto.name(), dto.description());
        return ResponseEntity.ok(ChannelResponse.Detail.from(channel));
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponse.Detail> createPrivateChannel(
            @Valid @RequestBody ChannelRequest.CreatePrivate req) {
        Channel channel = channelService.createPrivate(req.participantIds());
        return ResponseEntity.ok(ChannelResponse.Detail.from(channel));
    }

    @PatchMapping(params = "channelId")
    public ResponseEntity<ChannelResponse.Detail> updateChannel(
            @RequestParam UUID channelId,
            @Valid
            @RequestBody ChannelRequest.Update req) {
        Channel channel = channelService.update(channelId, req.newName(), req.newDescription());
        return ResponseEntity.ok(ChannelResponse.Detail.from(channel));
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<ChannelDto>> findByUser(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findByUser(userId));
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<String> joinChannel(@ModelAttribute ChannelRequest.join dto) {
        Channel channel = channelService.join(dto.userId(), dto.channelId());
        return ResponseEntity.ok(dto.userId() + " 님이 " + channel.getName() + "에 참여합니다.");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> findAll() {
        return ResponseEntity.ok(channelService.findAll());
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
