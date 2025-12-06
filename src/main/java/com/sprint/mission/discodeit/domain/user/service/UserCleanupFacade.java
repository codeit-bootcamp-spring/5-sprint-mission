package com.sprint.mission.discodeit.domain.user.service;

import com.sprint.mission.discodeit.common.cache.CacheHelper;
import com.sprint.mission.discodeit.common.cache.CacheName;
import com.sprint.mission.discodeit.domain.message.repository.MessageRepository;
import com.sprint.mission.discodeit.domain.notification.repository.NotificationRepository;
import com.sprint.mission.discodeit.domain.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.user.event.UserDeletedEvent;
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

        long nullifiedMessages = messageRepository.nullifyAuthorByUserId(userId);
        log.info("Nullified author for {} messages for userId={}", nullifiedMessages, userId);

        long deletedReadStatuses = readStatusRepository.deleteByUserId(userId);
        log.info("Deleted {} read statuses for userId={}", deletedReadStatuses, userId);

        long deletedNotifications = notificationRepository.deleteByReceiverId(userId);
        log.info("Deleted {} notifications for userId={}", deletedNotifications, userId);

        cacheHelper.evictCacheByKey(CacheName.READ_STATUSES, userId);
        cacheHelper.evictCacheByKey(CacheName.NOTIFICATIONS, userId);
    }
}
