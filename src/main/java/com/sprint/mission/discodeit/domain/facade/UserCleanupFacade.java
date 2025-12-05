package com.sprint.mission.discodeit.domain.facade;

import com.sprint.mission.discodeit.domain.event.user.UserDeletedEvent;
import com.sprint.mission.discodeit.domain.repository.MessageRepository;
import com.sprint.mission.discodeit.domain.repository.NotificationRepository;
import com.sprint.mission.discodeit.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.infra.cache.CacheHelper;
import com.sprint.mission.discodeit.infra.cache.CacheType;
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

        cacheHelper.evictCacheByKey(CacheType.READ_STATUSES, userId);
        cacheHelper.evictCacheByKey(CacheType.NOTIFICATIONS, userId);
    }
}
