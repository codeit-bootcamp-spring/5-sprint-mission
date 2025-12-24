package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

    @Value("${sse.timeout:1800000}")
    private long timeout;

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(timeout);

        emitter.onCompletion(()->{
            sseEmitterRepository.delete(receiverId, emitter);
        });
        emitter.onTimeout(() -> {
            sseEmitterRepository.delete(receiverId, emitter);
        });
        emitter.onError(ex->{
            sseEmitterRepository.delete(receiverId, emitter);
        });

        sseEmitterRepository.save(receiverId, emitter);

        if (lastEventId != null) {
            sendMissedMessages(emitter, lastEventId, receiverId);
        } else {
            ping(emitter);
        }

        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = SseMessage.createPrivate(receiverIds, eventName, data);
        sseMessageRepository.save(message);

        Map<UUID, List<SseEmitter>> emittersMap =
                sseEmitterRepository.findAllByReceiverIdsIn(receiverIds);

        log.debug("[SseService] SSE 전송 시작 - eventName: {}, receivers: {}",
                eventName, receiverIds.size());

        emittersMap.forEach((receiverId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .id(message.getEventId().toString())
                                    .name(message.getEventName())
                                    .data(message.getData())
                    );
                } catch (IOException e) {
                    log.warn("[SseService] SSE 전송 실패 - receiverId: {}, eventName: {}",
                            receiverId, eventName);

                    sseEmitterRepository.delete(receiverId, emitter);

                    try {
                        emitter.completeWithError(e);
                    } catch (Exception ex) {
                        log.debug("[SseService] completeWithError 실패 - receiverId: {}", receiverId);
                    }
                } catch (IllegalStateException e) {
                    log.debug("[SseService] 전송 완료된 SSE 전송 시도 취소- receiverId: {}", receiverId);
                    sseEmitterRepository.delete(receiverId, emitter);
                }
            }
        });
    }

    @Override
    public void broadcast(String eventName, Object data) {
        SseMessage message = SseMessage.createBroadcast(eventName, data);
        sseMessageRepository.save(message);

        Map<UUID, List<SseEmitter>> allEmitters = sseEmitterRepository.findAll();

        log.debug("[SseService] SSE 브로드캐스트 - eventName: {}, users: {}",
                eventName, allEmitters.size());
        log.debug("[SseService] Repository에서 가져온 Emitter 수: {}", allEmitters.size());

        allEmitters.forEach((receiverId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .id(message.getEventId().toString())
                                    .name(message.getEventName())
                                    .data(message.getData())
                    );
                } catch (IOException e) {
                    log.warn("[SseService] 브로드캐스트 실패 - receiverId: {}, eventName: {}",
                            receiverId, eventName);
                    sseEmitterRepository.delete(receiverId, emitter);

                    try {
                        emitter.completeWithError(e);
                    } catch (Exception ex) {
                        log.debug("[SseService] completeWithError 실패 - receiverId: {}", receiverId);
                    }

                } catch (IllegalStateException e) {
                    log.debug("[SseService] 이미 완료된 브로드캐스트 건너뜀 - receiverId: {}", receiverId);
                    sseEmitterRepository.delete(receiverId, emitter);
                }
            }
        });
    }

    @Scheduled(fixedRate = 3600000)
    @Override
    public void cleanUp() {
        sseEmitterRepository.findAll().values().stream()
                .flatMap(Collection::stream)
                .filter(emitter -> !ping(emitter))
                .forEach(emitter -> emitter.complete());
    }

    private boolean ping(SseEmitter emitter){
        try {
            emitter.send(SseEmitter.event().name("ping").build());
            return true;
        } catch (IOException | IllegalStateException e) {
            log.warn("[SseService] ping 전송 실패: {}", e.getMessage());
            return false;
        }
    }

    private void sendMissedMessages(SseEmitter emitter, UUID lastEventId, UUID receiverId) {
        List<SseMessage> missedMessages =
                sseMessageRepository.findAllByEventIdAfterAndReceiverId(lastEventId, receiverId);

        for (SseMessage msg : missedMessages) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(msg.getEventId().toString())
                                .name(msg.getEventName())
                                .data(msg.getData())
                );
            } catch (IOException | IllegalStateException e) {
                log.error("[SseService] 미전달 메시지 전송 실패 - 연결 종료: {}", e.getMessage());
                emitter.completeWithError(e);
                return; // 전달 실패 시 emitter 종료
            }
        }

        log.info("[SseService] 미전달 메시지 전송 완료 - receiverId: {}, 전송 수: {}",
                receiverId, missedMessages.size());
    }
}
