package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  // ✅ 단건 조회
  @GetMapping
  public ResponseEntity<UserStatus> findByUserId(@PathVariable UUID userId) {
    UserStatus status = userStatusService.findByUserId(userId);
    return ResponseEntity.ok(status);
  }

  // ✅ 상태 삭제
  @DeleteMapping
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userStatusService.delete(userId);
    return ResponseEntity.noContent().build();
  }


}
