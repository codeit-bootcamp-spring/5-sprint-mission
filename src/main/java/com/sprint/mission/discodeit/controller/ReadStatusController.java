package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // ✅ 특정 채널 메세지 수신 정보 생성
  @PostMapping
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity.status(201).body(created);
  }


  // ✅ 특정 채널 수신정보 수정
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updated);
  }


  // ✅ 특정 사용자 수신 정보 조회
  @GetMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> findById(@PathVariable UUID readStatusId) {
    ReadStatus found = readStatusService.findById(readStatusId);
    return ResponseEntity.ok(found);
  }

  // ✅ 유저별 전체 읽음 상태 조회
  @GetMapping
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  // ✅ 채널 기반 삭제
  @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteByChannelId(@PathVariable UUID channelId) {
    readStatusService.deleteByChannelId(channelId);
    return ResponseEntity.noContent().build();
  }

  // ✅ 단건 삭제
  @DeleteMapping("/{readStatusId}")
  public ResponseEntity<Void> delete(@PathVariable UUID readStatusId) {
    readStatusService.delete(readStatusId);
    return ResponseEntity.noContent().build();
  }
}
