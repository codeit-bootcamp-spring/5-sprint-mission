package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateResponse;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.global.api.ApiResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/channels")
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @PostMapping("/public")
    public ResponseEntity<ApiResponse<PublicChannelCreateResponse>> createPublic(
            @Valid @RequestBody PublicChannelCreateRequest request) {
        Channel created = channelService.create(request);
        PublicChannelCreateResponse response = new PublicChannelCreateResponse(
                request.name(),
                request.description()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // 비공개 채널 생성
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<PrivateChannelCreateResponse>> createPrivate(
            @Valid @RequestBody PrivateChannelCreateRequest request) {
        Channel created = channelService.create(request);
        PrivateChannelCreateResponse response = new PrivateChannelCreateResponse(
                request.participantIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // 공개 채널 정보 수정
    @PutMapping("/{channelId}")
    public ResponseEntity<ApiResponse<PublicChannelUpdateResponse>> update(
            @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request) {
        Channel updated = channelService.update(channelId, request);
        PublicChannelUpdateResponse response = new PublicChannelUpdateResponse(
                request.newName(),
                request.newDescription()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 채널 삭제
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChannelDto>>> listVisible(@RequestParam UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(channels));
    }

    // (편의) 단건 조회
    @GetMapping("/{channelId}")
    public ResponseEntity<ApiResponse<ChannelDto>> find(@PathVariable UUID channelId) {
        ChannelDto dto = channelService.find(channelId);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
