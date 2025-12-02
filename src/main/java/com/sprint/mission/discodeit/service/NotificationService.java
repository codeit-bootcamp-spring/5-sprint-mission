package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationDto createNotification(UUID receiverId, String title, String content);

    List<NotificationDto> getNotifications(UUID receiverId);

    void deleteNotification(UUID notificationId, UUID userId);
}