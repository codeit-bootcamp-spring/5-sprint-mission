package com.sprint.mission.discodeit.notification.presentation;

import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.notification.application.NotificationService;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> findAll(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        return notificationService.findAllByReceiverId(userDetails.getUserDetailsDto().id());
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void check(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails,
        @PathVariable UUID notificationId
    ) {
        notificationService.check(notificationId, userDetails.getUserDetailsDto().id());
    }
}
