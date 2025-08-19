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
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;


    //    [ ] 공개 채널을 생성할 수 있다.
    @RequestMapping(path = "createPublic", method = RequestMethod.POST)
    public ResponseEntity<Channel> createChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(201).body(channel);
    }


//[ ] 비공개 채널을 생성할 수 있다.

    @RequestMapping(path = "createPrivate", method = RequestMethod.POST)
    public ResponseEntity<Channel> createChannel(
            @RequestBody  PrivateChannelCreateRequest request
    ) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(201).body(channel);
    }
//[ ] 공개 채널의 정보를 수정할 수 있다.

    @RequestMapping(path = "update/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<Channel> updateChannel(
            @RequestBody  PublicChannelUpdateRequest request,
            @PathVariable UUID channelId
    ) {
        Channel channel = channelService.update(channelId, request);
        return ResponseEntity.status(201).body(channel);
    }


//            [ ] 채널을 삭제할 수 있다.
    @RequestMapping(path = "delete/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId){
        channelService.delete(channelId);
        return ResponseEntity.status(204).build();
    }

//            [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
    @RequestMapping(path="findAll/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllChannel(@PathVariable UUID userId){
        List<ChannelDto> channelList = channelService.findAllByUserId(userId);
        return ResponseEntity.status(200).body(channelList);
    }

}
