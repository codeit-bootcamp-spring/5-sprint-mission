package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageCreatedEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;

  @Async
  @TransactionalEventListener
  public void handle(MessageCreatedEvent event) {

    List<ReadStatus> statuses = readStatusRepository.findByChannelId(event.getChannelId());

    statuses.stream()
            .filter(ReadStatus::getNotificationEnabled)
            .filter(rs -> !rs.getUser()
                             .getId()
                             .equals(event.getAuthorId()))
            .forEach(rs -> {

              Notification notification = Notification.builder()
                                                      .receiver(rs.getUser())
                                                      .title(event.getTitle())
                                                      .content(event.getContent())
                                                      .type(NotificationType.MESSAGE_CREATED.name())
                                                      .build();

              notificationRepository.save(notification);
            });
  }
}