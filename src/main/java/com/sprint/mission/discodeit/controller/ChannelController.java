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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(path = "create/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest request
            ) {
        Channel publicChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    @RequestMapping(path = "create/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ){
        Channel privateChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }


    @RequestMapping(path = "findAll")
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId){
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }

    @RequestMapping(path = "update/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<Channel> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
    ){
        Channel updatedChannel = channelService.update(channelId, publicChannelUpdateRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @RequestMapping(path = "delete", method = RequestMethod.DELETE)
    public ResponseEntity<Channel> delete(@RequestParam("channelId") UUID channelId){
        channelService.delete(channelId);
        return ResponseEntity.ok().build();
    }


}
