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

// * [ ] 공개 채널을 생성할 수 있다.
// * [ ] 비공개 채널을 생성할 수 있다.
// * [ ] 공개 채널의 정보를 수정할 수 있다.
// * [ ] 채널을 삭제할 수 있다.
// * [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
            ) {
        Channel publicChannel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    @RequestMapping(path = "create/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
    ){
        Channel privateChannel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }

    @RequestMapping(path = "findBy/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@PathVariable("userId") UUID userId){
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
    public ResponseEntity<Channel> delete(@RequestParam UUID channelId){
        channelService.delete(channelId);
        return ResponseEntity.ok().build();
    }


}
