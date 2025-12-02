package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDto.DetailResponse;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final NotificationMapper notificationMapper;

  @Operation(summary = "알림 조회")
  @GetMapping
  public ResponseEntity<List<DetailResponse>> findAll() {

    return ResponseEntity.ok(notificationService.findAllByUserId(null)
                                                .stream()
                                                .map(notificationMapper::toDetailResponse)
                                                .toList());
  }

  @Operation(summary = "알림 확인")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {

    notificationService.delete(id);

    return ResponseEntity.noContent()
                         .build();
  }
}
