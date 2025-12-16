package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDTO;
import com.sprint.mission.discodeit.dto.request.NotificationCreateRequest;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final ApplicationEventPublisher applicationEventPublisher;
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Transactional
  public NotificationDTO createNotification(NotificationCreateRequest request) {
    Notification notification = Notification.builder()
        .receiverId(request.receiverId())
        .title(request.title())
        .content(request.content())
        .build();

    notificationRepository.save(notification);
    NotificationDTO notificationDTO = notificationMapper.toDto(notification);
    applicationEventPublisher.publishEvent(
        new NotificationCreatedEvent(request.receiverId(), notificationDTO));

    return notificationDTO;
  }

  @Cacheable(value = "notifications", key = "#receiverId")
  @Override
  @Transactional(readOnly = true)
  public List<NotificationDTO> findNotifications(UUID receiverId) {
    List<Notification> notifications = notificationRepository.findAllByReceiverId(receiverId);

    return notificationMapper.toDto(notifications);
  }

  @Caching(evict = {
      @CacheEvict(value = "notifications", key = "#receiverId")
  })
  @Override
  @Transactional
  public void checkNotifications(UUID notificationId, UUID receiverId) {

    notificationRepository.deleteById(notificationId);
  }
}
