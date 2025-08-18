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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(path="createPublic")
    public ResponseEntity<Channel>createPublicChannel(
            @RequestParam PublicChannelCreateRequest request
            ){
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @RequestMapping(path="createPrivate")
    public ResponseEntity<Channel>createPrivate(
            @RequestParam PrivateChannelCreateRequest request
    ){
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @RequestMapping(path="update")
    public ResponseEntity<Channel>update(
            @RequestParam("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
            ){
        Channel updatedChannel = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }

    @RequestMapping(path ="delete")
    public ResponseEntity<Void>delete(
            @RequestParam("channelId")UUID channelId
    ){
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @RequestMapping(path = "findAll")
    public ResponseEntity<List<ChannelDto>>findAll(
            @RequestParam("userId") UUID userId
    ){
        List<ChannelDto> channelList = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channelList);
    }



}
