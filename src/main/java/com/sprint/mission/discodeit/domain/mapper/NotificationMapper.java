package com.sprint.mission.discodeit.domain.mapper;

import com.sprint.mission.discodeit.domain.dto.notification.data.NotificationDto;
import com.sprint.mission.discodeit.domain.entity.Notification;
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
