package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value="/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(path="public",method= RequestMethod.POST)
    public ResponseEntity<Channel> publicChannel(@RequestPart PublicChannelCreateRequest request){
        Channel channel=channelService.create(request);
        return ResponseEntity.status(201).body(channel);
    }

    @RequestMapping(path="private",method= RequestMethod.POST)
    public ResponseEntity<Channel> privateChannel(@RequestPart PrivateChannelCreateRequest request){
        Channel channel=channelService.create(request);
        return ResponseEntity.status(201).body(channel);
    }

    @RequestMapping(path="update",method= RequestMethod.POST)
    public ResponseEntity<Channel> publicChannelUpdate(@RequestParam("channelId") UUID channelId, @RequestPart PublicChannelUpdateRequest request){
        Channel channel=channelService.update(channelId,request);
        return ResponseEntity.status(201).body(channel);
    }

    @RequestMapping(path="delete",method= RequestMethod.DELETE)
    public ResponseEntity<Channel> channelDelete(@RequestParam("channelId") UUID channelId){
        channelService.delete(channelId);

        return  ResponseEntity.status(204).body(null);
    }

    @RequestMapping(path="findChannelByUser", method=RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findChannelByUser(@RequestParam("userId") UUID userId){
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(200).body(channels);
    }



}
