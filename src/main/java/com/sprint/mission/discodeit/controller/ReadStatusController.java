package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  /* 메세지별 읽음표시
   * 내가 어디까지 읽었는지, 13시 10분 읽음 등
   * */

  // ✅ A채널 메세지 읽었을때 읽었음!이라는 새로운 읽음상태 생성
  @PostMapping
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity.status(201).body(created);
  }


  // ✅ 읽음상태 업데이트
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updated);
  }

  // ✅내가 속한 모든 채팅방에서, 내가 어디까지 읽었는지
  @GetMapping
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  // ✅ 채널 삭제시, 그 채널의 모든 읽음기록 지우기
  @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteByChannelId(@PathVariable UUID channelId) {
    readStatusService.deleteByChannelId(channelId);
    return ResponseEntity.noContent().build();
  }
}
