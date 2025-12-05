package com.sprint.mission.discodeit.domain.notification.repository;

import com.sprint.mission.discodeit.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(UUID receiverId);

    long deleteByReceiverId(UUID receiverId);
}
