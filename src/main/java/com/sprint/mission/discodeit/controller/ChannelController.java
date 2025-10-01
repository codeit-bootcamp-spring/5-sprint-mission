package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping(path = "public")
  public ResponseEntity<ChannelDto> create(@Validated @RequestBody PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 요청 수신: name={}, description={}", request.name(), request.description());

    ChannelDto createdChannel = channelService.create(request);

    log.info("공개 채널 생성 완료: channelId={}", createdChannel.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PostMapping(path = "private")
  public ResponseEntity<ChannelDto> create(@Validated @RequestBody PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 요청 수신: participants={}", request.participantIds());

    ChannelDto createdChannel = channelService.create(request);

    log.info("비공개 채널 생성 완료: channelId={}", createdChannel.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PatchMapping(path = "{channelId}")
  public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
                                           @Validated @RequestBody PublicChannelUpdateRequest request) {
    log.info("채널 수정 요청 수신: channelId={}, newName={}, newDescription={}",
            channelId, request.newName(), request.newDescription());

    ChannelDto updatedChannel = channelService.update(channelId, request);

    log.info("채널 수정 완료: channelId={}", updatedChannel.id());
    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    log.info("채널 삭제 요청 수신: channelId={}", channelId);

    channelService.delete(channelId);

    log.info("채널 삭제 완료: channelId={}", channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    log.info("사용자 채널 조회 요청 수신: userId={}", userId);

    List<ChannelDto> channels = channelService.findAllByUserId(userId);

    log.info("사용자 채널 조회 완료: userId={}, count={}", userId, channels.size());
    return ResponseEntity.status(HttpStatus.OK).body(channels);
  }
}