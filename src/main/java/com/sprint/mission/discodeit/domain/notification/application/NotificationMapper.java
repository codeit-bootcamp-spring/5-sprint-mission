package com.sprint.mission.discodeit.domain.notification.application;

import com.sprint.mission.discodeit.domain.notification.domain.Notification;
import com.sprint.mission.discodeit.domain.notification.presentation.dto.NotificationDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationDto(
            notification.getId(),
            notification.getCreatedAt(),
            notification.getReceiver().getId(),
            notification.getTitle(),
            notification.getContent()
        );
    }
}
