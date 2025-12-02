package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.notification.UnauthorizedNotificationAccessException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationDto createNotification(UUID receiverId, String title, String content) {
        Notification notification = new Notification(receiverId, title, content);
        Notification saved = notificationRepository.save(notification);

        log.info("[NotificationService] 알림 생성 완료 - receiverId: {}, title: {}", saved.getReceiverId(), saved.getTitle());

        return notificationMapper.toDto(saved);
    }

    @Override
    public List<NotificationDto> getNotifications(UUID receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withNotificationId(notificationId));

        if (!notification.getReceiverId().equals(userId)) {
            throw UnauthorizedNotificationAccessException.withDetails(notificationId, userId);
        }

        notificationRepository.delete(notification);
        log.info("[NotificationService] 알림 삭제 완료 - notificationId: {}, userId: {}", notificationId, userId);
    }
}