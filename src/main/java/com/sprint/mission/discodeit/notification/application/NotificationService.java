package com.sprint.mission.discodeit.notification.application;

import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.notification.domain.Notification;
import com.sprint.mission.discodeit.notification.domain.NotificationRepository;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationForbiddenException;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationNotFoundException;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

    @Transactional
    @CacheEvict(value = CacheName.NOTIFICATIONS, key = "#receiverId")
    public NotificationDto create(UUID receiverId, String title, String content) {
        User receiver = getUserOrThrow(receiverId);

        Notification notification = new Notification(receiver, title, content);
        Notification savedNotification = notificationRepository.save(notification);

        log.debug("알림 생성: notificationId={}, receiverId={}, title={}",
            savedNotification.getId(), receiverId, title);

        return notificationMapper.toDto(savedNotification);
    }

    @Cacheable(value = CacheName.NOTIFICATIONS, key = "#receiverId")
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("[Cache Miss] find all notifications: [receiverId={}]", receiverId);

        return notificationRepository.findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiverId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @Transactional
    @CacheEvict(value = CacheName.NOTIFICATIONS, key = "#requesterId")
    public void check(UUID notificationId, UUID requesterId) {
        Notification notification = getOrThrow(notificationId);

        if (notification.getReceiver() == null
            || !notification.getReceiver().getId().equals(requesterId)) {
            throw new NotificationForbiddenException(notificationId, requesterId);
        }

        if (notification.isChecked()) {
            log.debug("이미 확인된 알림: notificationId={}, receiverId={}",
                notificationId, requesterId);
            return;
        }

        notificationRepository.save(notification.check());

        log.debug("알림 확인: notificationId={}, receiverId={}",
            notificationId, requesterId);
    }

    @Transactional
    @CacheEvict(value = CacheName.NOTIFICATIONS, key = "#receiverId")
    public void deleteByReceiverId(UUID receiverId) {
        log.debug("사용자별 알림 삭제: receiverId={}", receiverId);
        notificationRepository.deleteAllByReceiverId(receiverId);
        log.info("사용자별 알림 삭제 완료: receiverId={}", receiverId);
    }

    private Notification getOrThrow(UUID notificationId) {
        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }

    private User getUserOrThrow(UUID receiverId) {
        return userRepository.findById(receiverId)
            .orElseThrow(() -> new UserNotFoundException(receiverId));
    }
}
