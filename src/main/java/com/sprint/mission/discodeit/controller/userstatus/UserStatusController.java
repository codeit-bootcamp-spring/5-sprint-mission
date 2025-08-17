package com.sprint.mission.discodeit.controller.userstatus;

import com.sprint.mission.discodeit.dto.request.status.UserStatusHeartbeatRequest;
import com.sprint.mission.discodeit.service.user.UserService;
import com.sprint.mission.discodeit.service.userstatus.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-statuses")
public class UserStatusController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(path = "/heartbeat")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void heartbeat(@Valid @RequestBody UserStatusHeartbeatRequest body) {
        userStatusService.heartbeat(body);
    }
}
