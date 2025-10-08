package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Channel", description = "채널 관리 API")
@RestController
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  //공개 채널 생성
  @Operation(summary = "공개 채널 생성")
  @PostMapping("/api/channels/public")
  public ResponseEntity<Void> createPublic(@RequestBody @Valid ChannelDto dto) {
    channelService.createPublicChannel(dto);
    log.info("공개 채널 생성 완료: {}", dto.getName());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  //비공개 채널 생성
  @Operation(summary = "비공개 채널 생성")
  @PostMapping("/api/channels/private")
  public ResponseEntity<Void> createPrivate(@RequestBody @Valid ChannelDto dto) {
    channelService.createPrivateChannel(dto);
    log.info("비공개 채널 생성 완료: {}", dto.getName());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  //채널 단건 조회: 서비스에서 DTO로 응답
  @Operation(summary = "채널 단건 조회")
  @GetMapping("/api/channels/{channelId}")
  public ResponseEntity<ChannelDto> findById(@PathVariable("channelId") UUID id) {
    log.info("채널 단건 조회 요청: id={}", id);
    ChannelDto channelDto = channelService.findById(id);
    return ResponseEntity.ok(channelDto);
  }

  //전체 채널 조회: 서비스에서 DTO 리스트로 응답
  @Operation(summary = "전체 채널 조회")
  @GetMapping("/api/channels")
  public ResponseEntity<List<ChannelDto>> findAll() {
    log.info("전체 채널 목록 조회 요청");
    List<ChannelDto> channelDtos = channelService.findAll();
    return ResponseEntity.ok(channelDtos);
  }


  //채널 수정: DTO만 넘김
  @Operation(summary = "채널 수정")
  @PatchMapping("/api/channels/{channelId}")
  public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID id,
      @RequestBody @Valid ChannelDto dto) {
    /*log.info("채널 수정 요청:id={}, name={}", id, dto.getName());
    channelService.update(id, dto);
    log.info("채널 수정 완료: id={}", id);
    return ResponseEntity.ok().build();*/
    ChannelDto updated = channelService.update(id, dto);
    log.info("채널 수정 완료: id={}", id);
    return ResponseEntity.ok(updated); // ✅ 수정 후 최신 DTO 응답
  }


  //채널 삭제
  @Operation(summary = "채널 삭제")
  @DeleteMapping("/api/channels/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID id) {
    channelService.delete(id);
    log.info("채널 삭제 완료: id={}", id);
    return ResponseEntity.noContent().build();
  }
}
