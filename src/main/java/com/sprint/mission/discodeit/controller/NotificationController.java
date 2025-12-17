package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserResponse().getId();
        log.info("[Controller] 알림 조회 요청 - userId: {}", userId);

        List<NotificationDto> notifications = notificationService.getNotifications(userId);

        log.info("[Controller] 알림 조회 성공 - userId: {}, count: {}", userId, notifications.size());
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserResponse().getId();
        log.info("[Controller] 알림 삭제 요청 - notificationId: {}, userId: {}", notificationId, userId);

        notificationService.deleteNotification(notificationId, userId);

        log.info("[Controller] 알림 삭제 성공 - notificationId: {}, userId: {}", notificationId, userId);
        return ResponseEntity.noContent().build();
    }
}