package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "읽음 상태 생성")
  @PostMapping
  public ResponseEntity<ReadStatus> create(@Valid @RequestBody ReadStatusCreateRequest request) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity
        .created(URI.create("/api/readStatuses/" + created.getId()))
        .body(created);
  }

  @Operation(summary = "읽음 상태 수정")
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "사용자의 읽음 상태 목록 조회")
  @GetMapping
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatuses);
  }
}

