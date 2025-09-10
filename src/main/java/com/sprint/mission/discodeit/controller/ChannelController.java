package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    // 공개
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest request) {
        ChannelDto created = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 비공개
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        ChannelDto created = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 수정
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        ChannelDto updated = channelService.update(channelId, request);
        return ResponseEntity.ok(updated);
    }

    // 삭제
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    // 유저별 채널 목록 조회
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }
}