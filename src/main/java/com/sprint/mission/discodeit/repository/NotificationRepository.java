package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
	List<NotificationDto> findAllByReceiverId(UUID receiverId);
}
