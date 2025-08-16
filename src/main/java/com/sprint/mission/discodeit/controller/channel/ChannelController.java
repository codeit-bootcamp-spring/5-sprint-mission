package com.sprint.mission.discodeit.controller.channel;

import com.sprint.mission.discodeit.dto.request.chnanel.ChannelCreateByDmRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.ChannelCreateByGuildRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.ChannelJoinLeaveRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.ChannelUpdateByGuildRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping(path = "/guild")
    public ResponseEntity<ChannelResponse> createGuildChannel(
            @Valid @RequestBody ChannelCreateByGuildRequest body
    ) {
        ChannelResponse res = channelService.createGuildChannel(body);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(res.id())
                .toUri();
        return ResponseEntity.created(location).body(res);
    }

    @PostMapping(path = "/dm")
    public ResponseEntity<ChannelResponse> createDmChannel(
            @Valid @RequestBody ChannelCreateByDmRequest body
    ) {
        ChannelResponse res = channelService.createDmChannel(body);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(res.id())
                .toUri();
        return ResponseEntity.created(location).body(res);
    }

    @PutMapping(path = "/guild/{id}")
    public ResponseEntity<Void> updateGuildChannel(
            @PathVariable("id") UUID channelId,
            @Valid @RequestBody ChannelUpdateByGuildRequest body
    ) {
        channelService.updateGuildChannel(channelId, body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/guild/{id}")
    public ResponseEntity<Void> deleteGuildChannel(
            @PathVariable("id") UUID channelId,
            @RequestParam("actorId") UUID actorId
    ) {
        channelService.deleteGuildChannel(channelId, actorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/dm/{id}/join")
    public ResponseEntity<Void> joinDm(
            @PathVariable("id") UUID channelId,
            @Valid @RequestBody ChannelJoinLeaveRequest body
    ) {
        channelService.joinDm(channelId, body.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/dm/{id}/leave")
    public ResponseEntity<Void> leaveDm(
            @PathVariable("id") UUID channelId,
            @Valid @RequestBody ChannelJoinLeaveRequest body
    ) {
        channelService.leaveDm(channelId, body.userId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/dm")
    public ResponseEntity<List<ChannelResponse>> findDmChannelsForUser(
            @RequestParam("userId") UUID userId
    ) {
        return ResponseEntity.ok(channelService.findDmChannelsForUser(userId));
    }
}
