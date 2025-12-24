package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.notification.UnauthorizedNotificationAccessException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @CacheEvict(value = "notifications", key = "#receiverId")
    @Transactional
    public NotificationDto createNotification(UUID receiverId, String title, String content) {
        Notification notification = new Notification(receiverId, title, content);
        Notification saved = notificationRepository.save(notification);

        log.info("[NotificationService] 알림 생성 완료 - receiverId: {}, title: {}", saved.getReceiverId(), saved.getTitle());

        NotificationDto dto = notificationMapper.toDto(saved);
        eventPublisher.publishEvent(new NotificationCreatedEvent(receiverId, dto));

        return notificationMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "notifications", key = "#receiverId")
    public List<NotificationDto> getNotifications(UUID receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        return notificationMapper.toDtoList(notifications);
    }

    // s3 업로드 중 실패 시 관리자에게 알림 전송을 위한 별도 트랜잭션
    // 로직 안에서 캐시 무효화
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyAdmins(String title, String content) {
        List<User> admins = userRepository.findUsersByRole(Role.ADMIN);
        int successCount = 0;
        int failCount = 0;

        Cache notificationsCache = cacheManager.getCache("notifications");

        for (User admin : admins) {
            try {
                Notification notification = new Notification(admin.getId(), title, content);
                notificationRepository.save(notification);
                successCount++;

                // 캐시 무효화
                if (notificationsCache != null) {
                    notificationsCache.evict(admin.getId());
                    log.debug("[NotificationService] notifications 캐시 evict - adminId={}", admin.getId());
                }
            } catch (Exception e) {
                log.error("[NotificationService] 관리자(ADMIN) {}에게 알림 전송 실패", admin.getId(), e);
                failCount++;
            }
        }

        log.info("[NotificationService] 관리자(ADMIN)에게 알림 전송 완료 - 성공: {}, 실패: {}, 제목: {}",
                successCount, failCount, title);
    }

    @Override
    @CacheEvict(value = "notifications", key = "#userId")
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