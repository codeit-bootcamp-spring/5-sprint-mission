package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.notification.domain.NotificationRepository;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupFacade {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    private final CacheService cacheService;

    @Transactional
    public void cleanup(UserDeletedEvent event) {
        UUID userId = event.userId();

        log.debug("Starting UserCleanup for userId={}", userId);

        try {
            int nullifiedMessages = messageRepository.nullifyAuthorByUserId(userId);
            int deletedReadStatuses = readStatusRepository.deleteAllByUserId(userId);
            int deletedNotifications = notificationRepository.deleteAllByReceiverId(userId);

            cacheService.evict(CacheName.READ_STATUSES, userId);
            cacheService.evict(CacheName.SUBSCRIBED_CHANNELS, userId);
            cacheService.evict(CacheName.NOTIFICATIONS, userId);

            log.info("UserCleanup completed: [userId={}, nullifiedMessages={}, deletedReadStatuses={}, deletedNotifications={}]",
                userId, nullifiedMessages, deletedReadStatuses, deletedNotifications);
        } catch (Exception e) {
            log.error("UserCleanup failed: [userId={}]", userId, e);
            throw e;
        }
    }
}
