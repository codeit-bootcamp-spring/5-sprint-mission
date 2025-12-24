package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.NotificationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class NotificationCreatedEvent {
    private final UUID receiverId;
    private final NotificationDto notificationDto;
}