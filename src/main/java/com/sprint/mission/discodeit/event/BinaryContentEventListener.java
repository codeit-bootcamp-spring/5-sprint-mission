package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final NotificationRepository notificationRepository;


  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {

    UUID id = event.getBinaryContentId();
    User user = event.getUser();

    try {
      binaryContentStorage.put(id, event.getBytes());
      updateStatusToSuccess(id, user);
    } catch (Exception ex) {
      updateStatusToFail(id, user);
      System.out.println(ex.getMessage());
    }
  }

  @Async
  @TransactionalEventListener
  public void handleBinaryContentCreated(BinaryContentFailEvent event) {

    updateStatusToFail(event.getBinaryContentId(), null);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatusToSuccess(UUID binaryContentId, User user) {
    BinaryContent bc = binaryContentRepository.findById(binaryContentId)
                                              .orElseThrow(() -> new BinaryContentNotFoundException(
                                                  binaryContentId));
    bc.updateStatus(BinaryContentStatus.SUCCESS.name());
    binaryContentRepository.save(bc);

    if (user == null) {
      return;
    }

    String requestId = MDC.get("requestId");

    Notification notification = Notification.builder()
                                            .receiver(user)
                                            .title("[Binary 업로드 성공]")
                                            .content("""
                                                RequestId: %s
                                                BinaryContentId: %s
                                                """.formatted(requestId, binaryContentId))
                                            .type(NotificationType.S3_UPLOAD_FAILED.name())
                                            .build();

    notificationRepository.save(notification);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatusToFail(UUID binaryContentId, User user) {
    BinaryContent bc = binaryContentRepository.findById(binaryContentId)
                                              .orElseThrow(() -> new BinaryContentNotFoundException(
                                                  binaryContentId));
    bc.updateStatus(BinaryContentStatus.FAIL.name());
    binaryContentRepository.save(bc);

    if (user == null) {
      return;
    }

    String requestId = MDC.get("requestId");

    Notification notification = Notification.builder()
                                            .receiver(user)
                                            .title("[Binary 업로드 실패]")
                                            .content("""
                                                RequestId: %s
                                                BinaryContentId: %s
                                                """.formatted(requestId, binaryContentId))
                                            .type(NotificationType.S3_UPLOAD_FAILED.name())
                                            .build();

    notificationRepository.save(notification);
  }
}
