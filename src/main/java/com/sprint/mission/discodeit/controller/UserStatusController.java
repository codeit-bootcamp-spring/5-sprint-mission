package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "UserStatus", description = "유저의 온라인 상태")
@RestController
@RequestMapping("/api/users/{userId}/userStatus")
@RequiredArgsConstructor
public class UserStatusController {

  private final UserStatusService userStatusService;

  //User의 온라인 상태를 갱신
  @Operation(summary = "온라인 상태 업데이트")
  @PatchMapping
  public ResponseEntity<Void> updateOnlineStatus(
      @Parameter(description = "유저의 UUID")
      @PathVariable UUID userId,
      @RequestBody UserStatusDto dto
  ) {
    userStatusService.updateUserStatusByUserId(userId, dto);
    log.info("온라인 상태 업데이트 완료: userId={}, newLastActiveAt={}", userId, dto.getNewLastActiveAt());
    return ResponseEntity.ok().build();
  }
}
