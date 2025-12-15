package com.sprint.mission.discodeit.notification.application;

import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.notification.domain.Notification;
import com.sprint.mission.discodeit.notification.domain.NotificationRepository;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationCheckForbiddenException;
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
        log.debug("Creating notification: [receiverId: {}, title: {}, content: {}]",
            receiverId, title, content);

        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new UserNotFoundException(receiverId));

        Notification savedNotification = notificationRepository.save(
            new Notification(receiver, title, content));

        NotificationDto result = notificationMapper.toDto(savedNotification);

        log.info("Notification created: [notificationId={}, receiverId={}, title={}]",
            result.id(), result.receiverId(), result.title());

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
        log.debug("Checking notification: [notificationId: {}, requesterId: {}]",
            notificationId, requesterId);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (notification.getReceiver() == null
            || !notification.getReceiver().getId().equals(requesterId)) {
            throw new NotificationCheckForbiddenException(notificationId, requesterId);
        }

        if (notification.isChecked()) {
            log.info("Already checked notification: [notificationId={}, receiverId={}]",
                notificationId, requesterId);
            return;
        }

        notification.check();

        log.info("Notification checked: [notificationId={}, receiverId={}]",
            notificationId, requesterId);
    }
}
