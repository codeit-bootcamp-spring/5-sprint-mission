package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

    @CacheEvict(value = "userNotifications", key = "#receiverId")
    public NotificationDto create(UUID receiverId, String title, String content) {
        User receiver = getUserOrThrow(receiverId);

        Notification notification = new Notification(receiver, title, content);
        Notification savedNotification = notificationRepository.save(notification);

        log.debug("Notification created: notificationId={}, receiverId={}, title={}",
            savedNotification.getId(), receiverId, title);

        return notificationMapper.toDto(savedNotification);
    }

    @Cacheable(value = "userNotifications", key = "#receiverId")
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("사용자 알림 목록 캐시 미스: receiverId={}", receiverId);
        return notificationRepository.findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiverId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @CacheEvict(value = "userNotifications", key = "#requesterId")
    public void check(UUID notificationId, UUID requesterId) {
        Notification notification = getOrThrow(notificationId);

        if (notification.getReceiver() == null
            || !notification.getReceiver().getId().equals(requesterId)) {
            throw new NotificationForbiddenException(notificationId, requesterId);
        }

        if (notification.isChecked()) {
            log.debug("Notification already checked: notificationId={}, receiverId={}",
                notificationId, requesterId);
            return;
        }

        notificationRepository.save(notification.check());

        log.debug("Notification checked: notificationId={}, receiverId={}",
            notificationId, requesterId);
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
