package com.sprint.mission.discodeit.sse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  @Value("${sse.timeout:1800000}")
  private long timeout;

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;


  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(timeout);

    sseEmitter.onCompletion(() -> {
      sseEmitterRepository.delete(receiverId, sseEmitter);
    });
    sseEmitter.onTimeout(() -> {
      sseEmitterRepository.delete(receiverId, sseEmitter);
    });
    sseEmitter.onError(ex -> {
      sseEmitterRepository.delete(receiverId, sseEmitter);
    });

    sseEmitterRepository.save(receiverId, sseEmitter);

    Optional.ofNullable(lastEventId)
        .ifPresentOrElse(
            id -> sseMessageRepository.findAllByEventIdAfterAndReceiverId(id, receiverId)
                .forEach(msg -> {
                  try {
                    sseEmitter.send(
                        SseEmitter.event()
                            .id(msg.getEventId().toString())
                            .name(msg.getEventName())
                            .data(msg.getData())
                    );
                  } catch (Exception e) {
                    log.error("Failed to send message: {}", e.getMessage());
                    sseEmitter.completeWithError(e);
                  }
                }),
            () -> ping(sseEmitter)
        );

    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(
        SseMessage.create(receiverIds, eventName, data));

    List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverIds);

    log.debug("SSE send. eventName : {}, receivers : {}, emitterCount : {}",
        eventName, receiverIds, emitters.size());

    emitters.forEach(sseEmitter -> {
      try {
        sseEmitter.send(
            SseEmitter.event()
                .id(message.getEventId().toString())
                .name(eventName)
                .data(data)
        );
      } catch (Exception e) {
        log.error("Failed to send DM or notification message: {}", e.getMessage());
        sseEmitter.completeWithError(e);
      }
    });
  }

  public void broadcast(String eventName, Object data) {
    log.debug("SSE broadcast. eventName={}", eventName);
    SseMessage message = sseMessageRepository.save(
        SseMessage.createBroadcast(eventName, data));
    sseEmitterRepository.findAll().forEach(sseEmitter -> {
      try {
        sseEmitter.send(
            SseEmitter.event()
                .id(message.getEventId().toString())
                .name(eventName)
                .data(data));
      } catch (Exception e) {
        log.error("Failed to send broadcast message: {}", e.getMessage());
        sseEmitter.completeWithError(e);
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.findAll().stream()
        .filter(emitter -> !ping(emitter))
        .forEach(ResponseBodyEmitter::complete);
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping").build());
      return true;
    } catch (IOException | IllegalStateException e) {
      log.debug("Failed to send ping event: {}", e.getMessage());
      return false;
    }
  }
}
