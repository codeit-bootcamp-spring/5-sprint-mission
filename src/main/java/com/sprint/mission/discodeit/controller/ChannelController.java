package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Channel 생성(PUBLIC)")
  @PostMapping("/public")
  public ResponseEntity<ChannelDto.DetailResponse> createPublicChannel(
      @RequestBody ChannelDto.CreateRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.create(request.toCommand(ChannelType.PUBLIC)).toResponse());
  }

  @Operation(summary = "Channel 생성(PRIVATE)")
  @PostMapping("/private")
  public ResponseEntity<ChannelDto.DetailResponse> createPrivateChannel(
      @RequestBody ChannelDto.CreateRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.create(request.toCommand(ChannelType.PRIVATE)).toResponse());
  }

  @Operation(summary = "Channel 생성(PUBLIC)")
  @PutMapping("/{id}")
  public ResponseEntity<ChannelDto.DetailResponse> updateChannel(@PathVariable UUID id,
      @RequestBody ChannelDto.UpdateRequest request) {

    return ResponseEntity.ok(channelService.update(request.toCommand(id)).toResponse());
  }

  @Operation(summary = "Channel 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {

    channelService.delete(id);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Channel 유저 별 조회")
  @GetMapping
  public ResponseEntity<List<ChannelDto.DetailResponse>> findUserChannels(
      @RequestParam UUID userId) {

    return ResponseEntity.ok(
        channelService.findAllByUserId(userId).stream().map(ChannelDto.Detail::toResponse)
            .toList());
  }
}
