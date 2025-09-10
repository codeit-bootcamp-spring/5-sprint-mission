package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @GetMapping
  @Override
  public ResponseEntity<List<ReadStatusDto>> findByUser(@RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(readStatusService.findByUser(userId));
  }

  @PutMapping("{channelId}")
  @Override
  public ResponseEntity<ReadStatusDto> markRead(
      @PathVariable("channelId") UUID channelId,
      @RequestParam("userId") UUID userId,
      @RequestBody(required = false) ReadStatusUpdateRequest request
  ) {
    return ResponseEntity.ok(readStatusService.markRead(userId, channelId, request));
  }
}
