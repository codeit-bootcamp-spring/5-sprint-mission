package com.sprint.mission.discodeit.controller;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
=======
import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // 특정 채널의 메시지 수신 정보 생성
  @PostMapping
  public ResponseEntity<ReadStatusResponseDto> create(
      @RequestBody ReadStatusCreateRequest request) {
    ReadStatusResponseDto response = readStatusService.create(request);
    return ResponseEntity.ok(response);
  }

  // 특정 채널의 메시지 수신 정보 수정
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusResponseDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatusResponseDto response = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(response);
  }

  // 특정 사용자의 메시지 수신 정보 조회 (쿼리 파라미터 방식)
  @GetMapping
  public ResponseEntity<List<ReadStatusResponseDto>> findAllByUser(@RequestParam UUID userId) {
    List<ReadStatusResponseDto> response = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(response);
  }

  // 특정 채널의 메시지 수신 정보 조회
  @GetMapping("/channel/{channelId}")
  public ResponseEntity<List<ReadStatusResponseDto>> findAllByChannel(
      @PathVariable UUID channelId) {
    List<ReadStatusResponseDto> response = readStatusService.findAllByChannelId(channelId);
    return ResponseEntity.ok(response);
=======
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @GetMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> find(@PathVariable UUID readStatusId) {
    log.debug("[READ_STATUS][FIND] id={}", readStatusId);
    return ResponseEntity.ok(readStatusService.find(readStatusId));
  }

  @PostMapping
  public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
    log.info("[ReadStatus][Create] userId = {}, channelId = {}", request.userId(), request.channelId());
    ReadStatusDto createdReadStatus = readStatusService.create(request);
    log.debug("[ReadStatus][Create][Done] id = {}", createdReadStatus.id());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @PatchMapping(path = "{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(@PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    log.info("[ReadStatus][Update] id = {}", readStatusId);
    ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
    log.debug("[ReadStatus][Update][Done] id = {}", updatedReadStatus.id());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
    log.debug("[ReadStatus][List] userId = {}", userId);
    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
  }
}
