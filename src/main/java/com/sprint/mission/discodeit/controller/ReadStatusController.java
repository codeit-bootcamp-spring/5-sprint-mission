package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  // TODO 나중에 로그인 중인 사용자만 처리하면 될듯?
  private final ChannelService channelService;
  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatusDto.DetailResponse> createReadStatus(
      @RequestBody ReadStatusDto.CreateRequest request) {
    return ResponseEntity.ok(readStatusService.create(request));
  }

  @PutMapping("/channels/{channelId}")
  public ResponseEntity<Void> updateReadStatusByChannelId(@PathVariable UUID channelId) {

    channelService.findById(channelId)
        .getParticipantIds()
        .forEach(userId -> {
          readStatusService.findAllByUserId(userId)
              .forEach(readStatus ->
              {
                readStatusService.update(readStatus.getId());
              });
        });
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateReadStatus(@PathVariable UUID id) {

    readStatusService.update(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto.DetailResponse>> getReadStatusByUser(
      @RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }
}
