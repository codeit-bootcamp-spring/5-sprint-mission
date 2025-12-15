package com.sprint.mission.discodeit.event.handler;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryEventHandler {
    private final BinaryContentStorage  binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;


    // 일단 잘 모르겠으니, 진행하며 비동기로 전환이 필요할 때, 다시 진행
    // 유저 생성때는 잘 동작하지만, 메시지에서는 동작하지 않는 것 같음. 일단 진행
    @Async(value = "ioExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAfterCommitCreate(BinaryContentCreatedEvent event) {
            // actuator로 시간 측정하기 위해 3초 딜레이
        log.info("[AFTER_COMMIT] binarycontent 생성 커밋 완료, {}", event.id());
        BinaryContentStatus status= BinaryContentStatus.PROCESSING;
        try{
            binaryContentStorage.put(event.id(),event.bytes());
        }catch (Exception e){
            log.error("실제 binary 저장 실패 : {}",e.getMessage());
            status = BinaryContentStatus.FAIL;
        }finally {
            if(status == BinaryContentStatus.PROCESSING){
                status = BinaryContentStatus.SUCCESS;
            }

            BinaryContent content = binaryContentRepository.findById(event.id())
                    .orElseThrow(()->BinaryContentNotFoundException.withId(event.id())
                    );
            UUID dId=content.getId();
            User user = userRepository.findByProfile(content);

            content = content.toBuilder()
                    .status(status)
                    .build();
            BinaryContent tar = binaryContentRepository.save(content);


            user = user.toBuilder()
                    .profile(tar)
                    .build();
            binaryContentRepository.deleteById(dId);
        }
    }
}
