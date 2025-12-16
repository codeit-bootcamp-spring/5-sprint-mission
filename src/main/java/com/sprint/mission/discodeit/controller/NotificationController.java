package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.exception.base.ErrorCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 내 알림 조회
     */
    @GetMapping
    public List<NotificationDto> getMyNotifications(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        UUID receiverId = userDetails.getUserDto().id();
        log.info("알림 목록 조회 요청: receiverId={}", receiverId);
        List<NotificationDto> notifications = notificationService.findAllByReceiverId(receiverId);
        log.debug("알림 목록 조회 응답: count={}", notifications.size());
        return ResponseEntity.ok(notifications);
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails,
            @PathVariable UUID notificationId
    ) {
        UUID receiverId = userDetails.getUserDto().id();
        log.info("알림 삭제 요청: id={}, receiverId={}", notificationId, receiverId);
        notificationService.delete(notificationId, receiverId);
        log.debug("알림 삭제 응답: id={}", notificationId);
        return ResponseEntity.noContent().build();
    }
}
