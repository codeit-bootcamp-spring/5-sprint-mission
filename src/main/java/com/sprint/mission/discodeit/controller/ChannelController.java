package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = {"/createPublic", "/create"}, method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(@RequestPart PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel publicChannel = channelService.createPublic(publicChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    @RequestMapping(value = "/createPrivate", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(@RequestPart PrivateChannelCreateRequest privateChannelCreateRequest) {
        Channel privateChannel = channelService.createPrivate(privateChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }

    @RequestMapping(value = {"/updatePublic", "/update"}, method = RequestMethod.POST)
    public ResponseEntity<Channel> updatePublicChannel(@RequestPart PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel updatedChannel = channelService.update(publicChannelUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Channel> deleteChannel(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = {"/listForUser/{id}", "/listForUser", "/listPublic"}, method = RequestMethod.GET)
    public ResponseEntity<List<ChannelFindResponse>> findAllByUserId(@PathVariable(value = "id", required = false) UUID id) {
        List<ChannelFindResponse> channelFindResponseList = channelService.findAllByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(channelFindResponseList);
    }


}
