package com.sprint.mission.discodeit.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(UUID receiverId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Notification n
        WHERE n.receiver.id = :receiverId
        """)
    int deleteAllByReceiverId(UUID receiverId);
}
