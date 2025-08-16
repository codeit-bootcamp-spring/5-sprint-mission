package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    // [POST] 공개 채널 생성
    @RequestMapping(path = "create/public", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest req) {
        Channel created = channelService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [POST] 비공개 채널 생성
    @RequestMapping(path = "create/private", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> createPrivate(@RequestBody PrivateChannelCreateRequest req) {
        Channel created = channelService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [PUT] 공개 채널 정보 수정
    @RequestMapping(path = "update/{channelId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest req
    ) {
        Channel updated = channelService.update(channelId, req);
        return ResponseEntity.ok(updated);
    }

    // [DELETE] 채널 삭제
    @RequestMapping(path = "delete/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    // [GET] 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(path = "list", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> list(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }
}
