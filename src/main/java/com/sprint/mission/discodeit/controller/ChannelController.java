package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(path = "/public", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDto> cratePublic(@RequestBody PublicChannelCreateRequest request) {
        Channel created = channelService.create(request);
        ChannelDto body = channelService.find(created.getId());
        return ResponseEntity
                .created(URI.create("/api/channels/" + body.id()))
                .body(body);
    }

    // 비공개 채널 생성
    @RequestMapping(path = "/private", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody PublicChannelCreateRequest request) {
        Channel created = channelService.create(request);
        ChannelDto body = channelService.find(created.getId());
        return ResponseEntity
                .created(URI.create("/api/channels/" + body.id()))
                .body(body);
    }

    // 채널 조회
    @RequestMapping(path = "/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<ChannelDto> find(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.find(channelId));
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    // 예: GET /api/channels?userId=xxxxx
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUser(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    // 공개 채널 정보 수정 (서비스에서 공개 채널만 수정하도록 검증)
    @RequestMapping(path = "/{channelId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
                                             @RequestBody PublicChannelUpdateRequest req) {
        Channel updated = channelService.update(channelId, req);
        ChannelDto body = channelService.find(updated.getId());
        return ResponseEntity.ok(body);
    }

    // 채널 삭제
    @RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

}
