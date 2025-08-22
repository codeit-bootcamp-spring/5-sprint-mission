package com.sprint.mission.discodeit.controller.userstatus;

import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.service.user.UserService;
import com.sprint.mission.discodeit.service.userstatus.UserStatusService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-statuses")
public class UserStatusController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public UserStatusResponse updateStatus(@RequestParam UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest body) {
    return userStatusService.updateStatusByUserId(userId, body);
  }

  @PostMapping("/heartbeat")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void heartbeat(@RequestParam UUID userId) {
    userStatusService.heartbeat(userId);
  }
}
