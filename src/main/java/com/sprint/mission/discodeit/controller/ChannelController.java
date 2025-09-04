package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Channel", description = "채널 관리 API")
@RestController
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  //공개 채널 생성: dto 객체로 넘기는 것으로 수정해야 함 (계층분리)
  @Operation(summary = "공개 채널 생성")
  @PostMapping("/api/channels/public")
  public ResponseEntity<UUID> createPublic(@RequestBody PublicChannelCreateRequest request) {
    Channel channel = request.toEntity(); // Entity 변환 메서드
    channelService.create(channel); // 저장
    return ResponseEntity.status(HttpStatus.CREATED).body(channel.getId());
  }


  //비공개 채널 생성: dto 객체로 넘기는 것으로 수정해야 함 (계층분리)
  @Operation(summary = "비공개 채널 생성")
  @PostMapping("/api/channels/private")
  public ResponseEntity<Void> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
    channelService.create(request.toEntity()); // Entity 변환 메서드
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  //채널 단건 조회
  @Operation(summary = "채널 단건 조회")
  @GetMapping("/api/channels/{channelId}")
  public ResponseEntity<Channel> findById(@PathVariable("channelId") UUID id) {
    Channel channel = channelService.findById(id);
    return ResponseEntity.ok(channel);
  }

  //전체 채널 조회
  @Operation(summary = "전체 채널 조회")
  @GetMapping("/api/channels")
  public ResponseEntity<List<Channel>> findAll() {
    return ResponseEntity.ok(channelService.findAll());
  }


  //채널 수정
  @Operation(summary = "채널 수정")
  @PatchMapping("/api/channels/{channelId}")
  public ResponseEntity<Void> update(@PathVariable("channelId") UUID id,
      @RequestBody PublicChannelUpdateRequest request) {
    channelService.update(id, request);
    return ResponseEntity.ok().build();
  }


  //채널 삭제
  @Operation(summary = "채널 삭제")
  @DeleteMapping("/api/channels/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID id) {
    channelService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
