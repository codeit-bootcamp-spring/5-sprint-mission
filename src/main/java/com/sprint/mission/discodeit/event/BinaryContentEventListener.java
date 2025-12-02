package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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


  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {

    UUID id = event.getBinaryContentId();
    try {
      binaryContentStorage.put(id, event.getBytes());
      updateStatusToSuccess(id);
    } catch (Exception ex) {
      updateStatusToFail(id);
      System.out.println(ex.getMessage());
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatusToSuccess(UUID binaryContentId) {
    BinaryContent bc = binaryContentRepository.findById(binaryContentId)
                                              .orElseThrow(() -> new BinaryContentNotFoundException(
                                                  binaryContentId));
    bc.updateStatus(BinaryContentStatus.SUCCESS.name());
    binaryContentRepository.save(bc);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatusToFail(UUID binaryContentId) {
    BinaryContent bc = binaryContentRepository.findById(binaryContentId)
                                              .orElseThrow(() -> new BinaryContentNotFoundException(
                                                  binaryContentId));
    bc.updateStatus(BinaryContentStatus.FAIL.name());
    binaryContentRepository.save(bc);
  }
}
