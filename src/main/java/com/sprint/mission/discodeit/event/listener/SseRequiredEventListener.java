package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.*;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
//@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

    private final SseService sseService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.debug("[SseRequiredEventListener] 알림 생성 이벤트 수신 - receiverId: {}",
                event.getReceiverId());

        sseService.send(
                List.of(event.getReceiverId()),
                "notifications.created",
                event.getNotificationDto()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentUpdated(BinaryContentUpdatedEvent event) {
        log.debug("[SseRequiredEventListener] 파일 상태 변경 이벤트 수신 - receiverId: {}",
                event.getReceiverId());

        sseService.send(
                List.of(event.getReceiverId()),
                "binaryContents.updated",
                event.getBinaryContentDto()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelCreated(ChannelCreatedEvent event) {
        log.debug("[SseRequiredEventListener] 채널 생성 이벤트 수신 - channelId: {}",
                event.getChannelResponse().getId());

        sseService.broadcast(
                "channels.created",
                event.getChannelResponse()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelUpdated(ChannelUpdatedEvent event) {
        log.debug("[SseRequiredEventListener] 채널 수정 이벤트 수신 - channelId: {}",
                event.getChannelResponse().getId());

        sseService.broadcast(
                "channels.updated",
                event.getChannelResponse()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelDeleted(ChannelDeletedEvent event) {
        log.debug("[SseRequiredEventListener] 채널 삭제 이벤트 수신 - channelId: {}",
                event.getChannelResponse().getId());

        sseService.broadcast(
                "channels.deleted",
                event.getChannelResponse()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        log.debug("[SseRequiredEventListener] 사용자 생성 이벤트 수신 - userId: {}",
                event.getUserResponse().getId());

        sseService.broadcast(
                "users.created",
                event.getUserResponse()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.debug("[SseRequiredEventListener] 사용자 수정 이벤트 수신 - userId: {}",
                event.getUserResponse().getId());

        sseService.broadcast(
                "users.updated",
                event.getUserResponse()
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {
        log.debug("[SseRequiredEventListener] 사용자 삭제 이벤트 수신 - userId: {}",
                event.getUserResponse().getId());

        sseService.broadcast(
                "users.deleted",
                event.getUserResponse()
        );
    }
}
