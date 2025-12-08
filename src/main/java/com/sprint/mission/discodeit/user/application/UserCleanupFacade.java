package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
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

    private final CacheHelper cacheHelper;

    @Transactional
    public void cleanup(UserDeletedEvent event) {
        UUID userId = event.userId();

        log.info("Starting UserCleanup for userId={}", userId);

        try {
            long nullifiedMessages = messageRepository.nullifyAuthorByUserId(userId);
            long deletedReadStatuses = readStatusRepository.deleteByUserId(userId);
            long deletedNotifications = notificationRepository.deleteByReceiverId(userId);

            log.debug("UserCleanup details: [userId={}, nullifiedMessages={}, deletedReadStatuses={}, deletedNotifications={}]",
                userId, nullifiedMessages, deletedReadStatuses, deletedNotifications);

            cacheHelper.evictCacheByKey(CacheName.READ_STATUSES, userId);
            cacheHelper.evictCacheByKey(CacheName.SUBSCRIBED_CHANNELS, userId);
            cacheHelper.evictCacheByKey(CacheName.NOTIFICATIONS, userId);

            log.info("UserCleanup completed: [userId={}]", userId);
        } catch (Exception e) {
            log.error("UserCleanup failed: [userId={}]", userId, e);
            throw e;
        }
    }
}
