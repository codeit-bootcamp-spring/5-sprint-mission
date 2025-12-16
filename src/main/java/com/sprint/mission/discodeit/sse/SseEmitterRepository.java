package com.sprint.mission.discodeit.sse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class SseEmitterRepository {

  private final ConcurrentHashMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID receiverId, SseEmitter emitter) {
    data.compute(receiverId, (key, emitters) -> {
      if (emitters == null) {
        return new CopyOnWriteArrayList<>(List.of(emitter));
      } else {
        emitters.add(emitter);
        return emitters;
      }
    });
    return emitter;
  }

  public Optional<List<SseEmitter>> findByReceiverId(UUID receiverId) {
    return Optional.ofNullable(data.get(receiverId));
  }

  public List<SseEmitter> findAllByReceiverId(Collection<UUID> receiverIds) {
    return data.entrySet().stream()
        .filter(entry -> receiverIds.contains(entry.getKey()))
        .map(Map.Entry::getValue)
        .flatMap(Collection::stream)
        .toList();
  }

  public List<SseEmitter> findAll() {
    return data.values().stream()
        .flatMap(Collection::stream)
        .toList();
  }

  public void delete(UUID receiverId, SseEmitter emitter) {
    data.computeIfPresent(receiverId, (key, emitters) -> {
      emitters.remove(emitter);
      return emitters.isEmpty() ? null : emitters;
    });
  }
}
