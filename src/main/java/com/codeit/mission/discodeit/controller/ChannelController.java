package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다.")
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }
        if (!StringUtils.hasText(request.name())) {
            throw new IllegalArgumentException("name이 필요합니다.");
        }

        Channel channel = channelService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @PostMapping("/private")
    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다.")
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }
        if (request.participantIds() == null || request.participantIds().isEmpty()) {
            throw new IllegalArgumentException("participantIds가 필요합니다.");
        }

        Channel channel = channelService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @PatchMapping("/{channelId}")
    @Operation(summary = "채널 수정", description = "해당 Id의 채널을 수정합니다.")
    public ResponseEntity<Channel> update(@PathVariable UUID channelId,
        @RequestBody PublicChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }

        Channel channel = channelService.update(channelId, request);

        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @DeleteMapping("/{channelId}")
    @Operation(summary = "채널 삭제", description = "해당 Id의 채널을 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId가 필요합니다.");
        }

        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "전체 채널 조회", description = "전체 채널을 조회합니다.")
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> allByUserId = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
    }
}
