package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.PublicChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(path = "public/create", method = RequestMethod.POST)
    public ResponseEntity<PublicChannelResponseDto> createPublicChannel(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        PublicChannelResponseDto dto = channelService.createPublicChannel(publicChannelCreateRequest);
        URI location = URI.create("/channels/" + dto.id());
        return ResponseEntity.created(location).body(dto);
    }

    @RequestMapping(path = "private/create", method = RequestMethod.POST)
    public ResponseEntity<PrivateChannelResponseDto> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        PrivateChannelResponseDto dto = channelService.createPrivateChannel(privateChannelCreateRequest);
        URI location = URI.create("/channels/" + dto.id());
        return ResponseEntity.created(location).body(dto);
    }

    @RequestMapping(path = "public/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<PublicChannelResponseDto> findPublicChannel(@PathVariable("id") UUID channelId) {
        PublicChannelResponseDto responseDto = channelService.findPublicChannel(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @RequestMapping(path = "private/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<PrivateChannelResponseDto> findPrivateChannel(@PathVariable("id") UUID channelId) {
        PrivateChannelResponseDto responseDto = channelService.findPrivateChannel(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @RequestMapping(path = "findAllByUser/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> findAllByUser(@PathVariable("userId") UUID userId) {
        List<ChannelResponse> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }

    @RequestMapping(path = "update", method = RequestMethod.PUT)
    public ResponseEntity<Channel> updateChannel(@RequestBody ChannelUpdateRequest channelUpdateRequest) {
        Channel updated = channelService.update(channelUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @RequestMapping(path = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable("id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @RequestMapping(path = "{channelId}/participants", method = RequestMethod.GET)
    public ResponseEntity<List<UUID>> getParticipants(@PathVariable UUID channelId) {
        List<UUID> participants = channelService.findParticipantsByChannelId(channelId);
        return ResponseEntity.ok(participants);
    }

    @RequestMapping(path = "{channelId}/participants", method = RequestMethod.POST)
    public ResponseEntity<Void> addParticipant(@PathVariable UUID channelId, @RequestBody UUID userId) {
        channelService.addParticipant(channelId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(path = "{channelId}/participants/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeParticipant(@PathVariable UUID channelId, @PathVariable UUID userId) {
        channelService.removeParticipant(channelId, userId);
        return ResponseEntity.ok().build();
    }
}
