package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEventListener {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SseService sseService;

  @Async(value = "taskExecutor")
  @EventListener
  public void onNotificationCreated(NotificationCreatedEvent event) {
    sseService.send(List.of(event.receiverId()), "notifications.created", event.notificationDTO());
  }

  @Async(value = "taskExecutor")
  @EventListener
  public void onBinaryContentUpdated(BinaryContentUpdatedEvent event) {
    sseService.broadcast("binaryContents.updated", event.binaryContentDTO());
  }

  @Async(value = "taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onChannelUpdated(ChannelUpdatedEvent event) {
    if (event.channelDTO().type().equals(ChannelType.PUBLIC)) {
      sseService.broadcast(event.name(), event.channelDTO());
    } else {
      Set<UUID> receiverIds = event.channelDTO().participants().stream()
          .map(UserDTO::id)
          .collect(Collectors.toSet());
      sseService.send(receiverIds, event.name(), event.channelDTO());
    }

  }

  @Async(value = "taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserUpdated(UserUpdatedEvent event) {
    sseService.broadcast(event.name(), event.userDTO());
  }

  @Async(value = "taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserLogInOutEvent(UserLogInOutEvent event) {
    User user = userRepository.findById(event.userId()).orElse(null);
    UserDTO userDTO = userMapper.toDto(user);
    sseService.broadcast("users.updated", userDTO);
  }
}
