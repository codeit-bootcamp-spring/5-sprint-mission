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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        User receiver = getUserOrThrow(receiverId);
        return notificationRepository.findAllByReceiverAndCheckedFalseOrderByCreatedAtDesc(receiver)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @Transactional
    public void check(UUID notificationId, UUID requesterId) {
        Notification notification = getOrThrow(requesterId);
        if (!notification.getReceiver().getId().equals(requesterId)) {
            throw new NotificationForbiddenException(notificationId, requesterId);
        }

        notification.check();
        log.debug("Notification checked: notificationId={}, receiverId={}",
            notificationId, requesterId);
    }

    @Transactional
    public NotificationDto create(UUID receiverId, String title, String content) {
        User receiver = getUserOrThrow(receiverId);

        Notification notification = new Notification(receiver, title, content);
        Notification saved = notificationRepository.save(notification);

        log.debug("Notification created: notificationId={}, receiverId={}, title={}",
            saved.getId(), receiverId, title);

        return notificationMapper.toDto(saved);
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
