package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RoleUpdatedEventListener {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @TransactionalEventListener
  public void handle(RoleUpdatedEvent event) {

    User user = userRepository.findById(event.getUserId())
                              .orElse(null);

    if (user == null) {
      return;
    }

    Notification notification = Notification.builder()
                                            .receiver(user)
                                            .title("권한이 변경되었습니다.")
                                            .content(String.format("%s -> %s", event.getOldRole(),
                                                event.getNewRole()))
                                            .type(NotificationType.ROLE_UPDATED.name())
                                            .build();

    notificationRepository.save(notification);
  }
}