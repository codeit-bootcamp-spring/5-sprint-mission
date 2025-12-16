package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.NotificationDTO;
import java.util.UUID;

public record NotificationCreatedEvent(
    UUID receiverId,
    NotificationDTO notificationDTO
) {

}
