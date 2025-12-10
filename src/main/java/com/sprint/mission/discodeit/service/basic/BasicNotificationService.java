package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto.Detail;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Cacheable(value = "notification-list", key = "#userId")
  public List<Detail> findAllByUserId(UUID userId) {
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId)
                                 .stream()
                                 .map(notificationMapper::toDetail)
                                 .toList();
  }

  @Override
  @Transactional
  @CacheEvict(value = "notification-list", allEntries = true)
  public void delete(UUID id) {
    notificationRepository.deleteById(id);
  }
}
