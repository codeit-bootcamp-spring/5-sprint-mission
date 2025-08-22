package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Channel")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "사용자가 참여 중인 채널 목록 조회")
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channels);
  }

  @Operation(summary = "퍼블릭 채널 생성")
  @PostMapping("/public")
  public ResponseEntity<Channel> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request) {
    Channel created = channelService.create(request); // 퍼블릭 생성 처리
    return ResponseEntity
        .created(URI.create("/api/channels/" + created.getId()))
        .body(created);
  }

  @Operation(summary = "프라이빗 채널 생성")
  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request) {
    Channel created = channelService.create(request); // 프라이빗 생성 처리
    return ResponseEntity
        .created(URI.create("/api/channels/" + created.getId()))
        .body(created);
  }

  @Operation(summary = "채널 정보 수정")
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> update(@PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {
    Channel updated = channelService.update(channelId, request);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "채널 삭제")
  @DeleteMapping("/{channelId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
  }
}
