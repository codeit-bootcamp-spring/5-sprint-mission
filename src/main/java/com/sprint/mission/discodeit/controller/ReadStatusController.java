package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // ✅ 특정 채널 메세지 수신 정보 생성
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ReadStatusCreateRequest request) {
        readStatusService.create(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 특정 사용자 수신 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReadStatus> findById(@PathVariable UUID id) {
        ReadStatus found = readStatusService.findById(id);
        return ResponseEntity.ok(found);
    }

    // ✅ 유저별 전체 읽음 상태 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    // ✅ 특정 채널 수신정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody ReadStatusUpdateRequest request) {
        readStatusService.update(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 채널 기반 삭제
    @DeleteMapping("/channel/{channelId}")
    public ResponseEntity<Void> deleteByChannelId(@PathVariable UUID channelId) {
        readStatusService.deleteByChannelId(channelId);
        return ResponseEntity.noContent().build();
    }

    // ✅ 단건 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        readStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
