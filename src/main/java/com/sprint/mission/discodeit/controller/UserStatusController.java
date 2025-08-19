package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    /**
     * 하트비트(접속갱신): 클라이언트가 주기적으로 호출
     * 사용자가 온라인 상태임을 갱신하는 용
     */
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<String> heartbeat(@PathVariable("userId") UUID userId) {
        userStatusService.updateLastAccessedAt(userId);
        boolean online = userStatusService.isOnline(userId);
        return ResponseEntity.ok(online ? "온라인" : "오프라인");
    }

    /**
     * 현재 온라인 상태 조회
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserStatusDto.response> getStatus(@PathVariable("userId") UUID userId) {
        boolean online = userStatusService.isOnline(userId);
        Instant last = userStatusService.findByUserId(userId).map(UserStatus::getLastAccessedAt).orElse(null);

        return ResponseEntity.ok(new UserStatusDto.response(userId, online, last));
    }
}
