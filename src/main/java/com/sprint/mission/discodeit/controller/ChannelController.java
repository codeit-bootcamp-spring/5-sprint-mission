package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  // 공개/비공개 채널 생성
  @PostMapping("/public")
  public ResponseEntity<ChannelResponseDto> createPublic(
      @RequestBody ChannelCreateRequest request) {
    ChannelResponseDto response = channelService.create(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelResponseDto> createPrivate(
      @RequestBody PrivateChannelCreateRequest request) {
    ChannelResponseDto response = channelService.create(request);
    return ResponseEntity.ok(response);
  }

  // 채널 정보 수정 (공개 채널 등)
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponseDto> update(
      @PathVariable UUID channelId,
      @RequestBody ChannelUpdateRequest request
  ) {
    ChannelResponseDto response = channelService.update(channelId, request);
    return ResponseEntity.ok(response);
  }

  // 채널 삭제
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  // 특정 사용자가 볼 수 있는 채널 목록 조회
  @GetMapping
  public ResponseEntity<List<ChannelResponseDto>> findAllByUserId(@RequestParam UUID userId) {
    List<ChannelResponseDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channels);
  }
}
