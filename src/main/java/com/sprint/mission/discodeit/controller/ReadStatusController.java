package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.sub.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 생성
    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus created = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 수정
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus updated = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(updated);
    }

    // 특정 User ReadStatus 목록 조회
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
        List<ReadStatus> statuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(statuses);
    }
}