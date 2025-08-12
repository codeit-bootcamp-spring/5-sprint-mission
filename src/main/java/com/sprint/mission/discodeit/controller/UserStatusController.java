package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user/status")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    /**
     * 하트비트(접속갱신): 클라이언트가 주기적으로 호출
     * 사용자가 온라인 상태임을 갱신하는 용
     */
    @RequestMapping(value = "/{id}/heartbeat", method = RequestMethod.POST)
    public ResponseEntity<Void> heartbeat(@PathVariable("id") UUID userId) {
        userStatusService.updateLastAccessedAt(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 온라인 상태 조회
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserStatusDto.response> getStatus(@PathVariable("id") UUID userId) {

        // 사용자 아이디로 상태 가져오기
        UserStatus userStatus = userStatusService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 온라인 상태를 찾을 수 없습니다."));


        return ResponseEntity.ok(new UserStatusDto.response(
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastAccessedAt()
                ));
    }
}
