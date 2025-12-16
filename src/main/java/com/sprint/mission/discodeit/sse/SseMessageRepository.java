package com.sprint.mission.discodeit.sse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SseMessageRepository {

  @Value("${sse.event-queue-capacity:100}")
  private int eventQueueCapacity;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(SseMessage message) {
    int availableCapacity = eventQueueCapacity - eventIdQueue.size();
    while (availableCapacity < 1) {
      UUID removed = eventIdQueue.removeFirst();
      messages.remove(removed);
      availableCapacity++;
    }

    UUID eventId = message.getEventId();
    eventIdQueue.addLast(eventId);
    messages.put(eventId, message);
    return message;
  }

  public List<SseMessage> findAllByEventIdAfterAndReceiverId(UUID eventId, UUID receiverId) {
    return eventIdQueue.stream()
        .dropWhile(id -> !id.equals(eventId))
        .skip(1)
        .map(messages::get)
        .filter(msg -> msg != null && msg.isReceivable(receiverId))
        .toList();
  }
}
