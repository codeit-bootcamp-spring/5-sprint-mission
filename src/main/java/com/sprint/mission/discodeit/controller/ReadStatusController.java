package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto.Detail;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  // TODO 나중에 로그인 중인 사용자만 처리하면 될듯?
  private final ReadStatusService readStatusService;

  @Operation(summary = "Read Status 생성")
  @PostMapping
  public ResponseEntity<ReadStatusDto.DetailResponse> createReadStatus(
      @RequestBody CreateRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusService.create(request.toCreate()).toDetailResponse());
  }

  @Operation(summary = "Read Status 수정")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateReadStatus(@PathVariable UUID id) {

    readStatusService.update(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Read Status 유저 별 조회")
  @GetMapping
  public ResponseEntity<List<ReadStatusDto.DetailResponse>> getReadStatusByUser(
      @RequestParam UUID userId) {

    return ResponseEntity.ok(
        readStatusService.findAllByUserId(userId).stream().map(Detail::toDetailResponse).toList());
  }
}
