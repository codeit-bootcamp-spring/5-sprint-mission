package com.sprint.mission.discodeit.domain.notification.service;

import com.sprint.mission.discodeit.domain.notification.dto.data.NotificationDto;
import com.sprint.mission.discodeit.domain.notification.entity.Notification;
import com.sprint.mission.discodeit.domain.notification.exception.NotificationForbiddenException;
import com.sprint.mission.discodeit.domain.notification.exception.NotificationNotFoundException;
import com.sprint.mission.discodeit.domain.notification.mapper.NotificationMapper;
import com.sprint.mission.discodeit.domain.notification.repository.NotificationRepository;
import com.sprint.mission.discodeit.domain.user.entity.User;
import com.sprint.mission.discodeit.domain.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.domain.user.repository.UserRepository;
import com.sprint.mission.discodeit.infrastructrue.cache.CacheType;
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
    @CacheEvict(value = CacheType.NOTIFICATIONS, key = "#receiverId")
    public NotificationDto create(UUID receiverId, String title, String content) {
        User receiver = getUserOrThrow(receiverId);

        Notification notification = new Notification(receiver, title, content);
        Notification savedNotification = notificationRepository.save(notification);

        log.debug("알림 생성: notificationId={}, receiverId={}, title={}",
            savedNotification.getId(), receiverId, title);

        return notificationMapper.toDto(savedNotification);
    }

    @Cacheable(value = CacheType.NOTIFICATIONS, key = "#receiverId")
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("사용자 알림 목록 캐시 미스: receiverId={}", receiverId);
        return notificationRepository.findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiverId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @Transactional
    @CacheEvict(value = CacheType.NOTIFICATIONS, key = "#requesterId")
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
    @CacheEvict(value = CacheType.NOTIFICATIONS, key = "#receiverId")
    public void deleteByReceiverId(UUID receiverId) {
        log.debug("사용자별 알림 삭제: receiverId={}", receiverId);
        notificationRepository.deleteByReceiverId(receiverId);
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
