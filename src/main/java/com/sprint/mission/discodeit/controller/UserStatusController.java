package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusHeartbeatRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-statuses")
public class UserStatusController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(path = "/heartbeat")
    public ResponseEntity<Void> heartbeat(@Valid @RequestBody UserStatusHeartbeatRequest body) {
        userStatusService.heartbeat(body);
        return ResponseEntity.noContent().build();
    }
}
