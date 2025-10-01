package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatusDto> create(@Validated @RequestBody ReadStatusCreateRequest request) {
    log.info("읽음 상태 생성 요청 수신: userId={}, channelId={}",
            request.userId(), request.channelId());

    ReadStatusDto createdReadStatus = readStatusService.create(request);

    log.info("읽음 상태 생성 완료: readStatusId={}", createdReadStatus.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
  }

  @PatchMapping(path = "{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
          @PathVariable("readStatusId") UUID readStatusId,
          @Validated @RequestBody ReadStatusUpdateRequest request
  ) {
    log.info("읽음 상태 수정 요청 수신: readStatusId={}, newLastReadAt={}",
            readStatusId, request.newLastReadAt());

    ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);

    log.info("읽음 상태 수정 완료: readStatusId={}", updatedReadStatus.id());
    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
    log.info("사용자별 읽음 상태 조회 요청 수신: userId={}", userId);

    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);

    log.info("사용자별 읽음 상태 조회 완료: userId={}, count={}", userId, readStatuses.size());
    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }
}