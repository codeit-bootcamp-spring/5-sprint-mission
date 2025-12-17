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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private static final String NOTIFICATIONS_CACHE = "notifications";

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CacheManager cacheManager;

    @Cacheable(value = "notifications", key = "#receiverId", unless = "#result.isEmpty()")
    @PreAuthorize("principal.userDto.id == #receiverId")
    @Override
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("알림목록 조회 시작 : receiverId = {}", receiverId);

        List<NotificationDto> notifications =
                notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
                        .stream()
                        .map(notificationMapper::toDto)
                        .toList();

        log.info("알림목록 조회 완료 : receiverId = {}, 조회된 항목 수 = {}", receiverId, notifications.size());
        return notifications;
    }

    @CacheEvict(value = "notifications", key = "#receiverId")
    @PreAuthorize("principal.userDto.id == #receiverId")
    @Transactional
    @Override
    public void delete(UUID notificationId, UUID receiverId) {
        log.debug("알림 삭제 시작 : id = {}, receiverId = {}", notificationId, receiverId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

        // 본인 알림만 삭제 가능
        if (!notification.getReceiverId().equals(receiverId)) {
            log.warn("알림 삭제 권한 부족 : id = {}, receiverId = {}", notificationId, receiverId);
            throw NotificationForbiddenException.withId(notificationId, receiverId);
        }

        notificationRepository.delete(notification);
        log.info("알림 삭제 완료 : id = {}, receiverId = {}", notificationId, receiverId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void create(Set<UUID> receiverIds, String title, String content) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            log.warn("알림 생성 요청 비어있음 : receiverIds = {}", receiverIds);
            return;
        }

        log.debug("새 알림 생성 시작 :  receiverIds = {}, title = {}", receiverIds, title);

        List<Notification> notifications = receiverIds.stream()
                .map(receiverId -> new Notification(receiverId, title, content))
                .toList();

        notificationRepository.saveAll(notifications);

        evictNotificationCache(receiverIds);

        log.info("새 알림 생성 완료 : receiverIds = {}",  receiverIds);
    }

    // 캐시 무효화
    private void evictNotificationCache(Set<UUID> receiverIds) {

        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }

        Cache cache = cacheManager.getCache(NOTIFICATIONS_CACHE);
        if (cache != null) {
            for (UUID receiverId : receiverIds) {
                cache.evict(receiverId);
            }
            log.debug("알림 캐시 제거 완료 : receiverCount = {}", receiverIds.size());
        } else {
            log.warn("알림 캐시가 존재하지 않음.");
        }
    }


}
