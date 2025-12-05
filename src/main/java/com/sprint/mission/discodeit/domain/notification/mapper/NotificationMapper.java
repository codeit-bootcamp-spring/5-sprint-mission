package com.sprint.mission.discodeit.domain.notification.mapper;

import com.sprint.mission.discodeit.domain.notification.dto.NotificationDto;
import com.sprint.mission.discodeit.domain.notification.entity.Notification;
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
