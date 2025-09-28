package com.sprint.mission.discodeit.controller;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
=======
import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
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
=======
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping(path = "public")
  public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
    log.info("[CHANNEL][CREATE_PUBLIC] name={}", request.name());
    ChannelDto createdChannel = channelService.create(request);
    log.debug("[CHANNEL][CREATE_PUBLIC][DONE] id={}", createdChannel.id());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PostMapping(path = "private")
  public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
    log.info("[CHANNEL][CREATE_PRIVATE] members={}",request.participantIds().size());
    ChannelDto createdChannel = channelService.create(request);
    log.debug("[CHANNEL][CREATE_PRIVATE][DONE] id={}", createdChannel.id());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PatchMapping(path = "{channelId}")
  public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    log.info("[CHANNEL][UPDATE] id={}", channelId);
    ChannelDto updatedChannel = channelService.update(channelId, request);
    log.debug("[CHANNEL][UPDATE][DONE] id={}", updatedChannel.id());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannel);
  }

  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    log.warn("[CHANNEL][DELETE] id={}", channelId);
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    log.debug("[CHANNEL][LIST] userId={}", userId);
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
  }
}
