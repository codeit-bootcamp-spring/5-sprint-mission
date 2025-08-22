package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/userStatus")
@RequiredArgsConstructor
public class UserStatusController {

  private final UserStatusService userStatusService;

  // ✅ User 온라인 상태 업데이트
  @PatchMapping
  public ResponseEntity<Void> updateOnlineStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    userStatusService.updateUserStatusByUserId(userId, request);
    return ResponseEntity.ok().build();
  }
}
