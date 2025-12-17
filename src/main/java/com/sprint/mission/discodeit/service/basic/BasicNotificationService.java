package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void create(Set<UUID> receiverIds, String title, String content) {
        List<Notification> notifications = receiverIds.stream()
                .map(receiverId -> new Notification(receiverId, title, content))
                .toList();

        notificationRepository.saveAll(notifications);
        log.info("새 알림 생성: receiverIds={}", receiverIds);
    }

    @Cacheable(value = "notifications", key = "#receiverId", unless = "#result.isEmpty()")
    @Override
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("알림 목록 조회 시작: receiverId={}", receiverId);
        List<NotificationDto> notifications = notificationRepository
                .findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
        log.info("알림 목록 조회 완료: receiverId={}, 조회된 항목 수={}", receiverId, notifications.size());
        return notifications;
    }

    @CacheEvict(value = "notifications", key = "#receiverId")
    @Override
    public void delete(UUID notificationId, UUID receiverId) {
        log.debug("알림 삭제 시작: id={}, receiverId={}", notificationId, receiverId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        if (!notification.getReceiverId().equals(receiverId)) {
            log.warn("알림 삭제 권한 없음: id={}, receiverId={}", notificationId, receiverId);
            throw new NotificationForbiddenException();
        }
        notificationRepository.delete(notification);
    }
}
