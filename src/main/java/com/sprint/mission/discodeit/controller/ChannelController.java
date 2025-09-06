package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
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

  //공개 채널 생성: dto만 넘김
  @Operation(summary = "공개 채널 생성")
  @PostMapping("/api/channels/public")
  public ResponseEntity<Void> createPublic(@RequestBody ChannelDto dto) {
    channelService.createPublicChannel(dto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  //비공개 채널 생성: dto만 넘김
  @Operation(summary = "비공개 채널 생성")
  @PostMapping("/api/channels/private")
  public ResponseEntity<Void> createPrivate(@RequestBody ChannelDto dto) {
    channelService.createPrivateChannel(dto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  //채널 단건 조회: 서비스에서 DTO로 응답
  @Operation(summary = "채널 단건 조회")
  @GetMapping("/api/channels/{channelId}")
  public ResponseEntity<ChannelDto> findById(@PathVariable("channelId") UUID id) {
    ChannelDto channelDto = channelService.findById(id);
    return ResponseEntity.ok(channelDto);
  }

  //전체 채널 조회: 서비스에서 DTO 리스트로 응답
  @Operation(summary = "전체 채널 조회")
  @GetMapping("/api/channels")
  public ResponseEntity<List<ChannelDto>> findAll() {
    List<ChannelDto> channelDtos = channelService.findAll();
    return ResponseEntity.ok(channelDtos);
  }


  //채널 수정: DTO만 넘김
  @Operation(summary = "채널 수정")
  @PatchMapping("/api/channels/{channelId}")
  public ResponseEntity<Void> update(@PathVariable("channelId") UUID id,
      @RequestBody ChannelDto dto) {
    channelService.update(id, dto); //Dto를 직접 서비스로 전달
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
