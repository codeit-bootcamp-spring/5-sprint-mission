package com.sprint.mission.discodeit.repository.impl;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Repository
public class InMemorySseMessageRepository implements SseMessageRepository {
    @Value("${sse.event-queue-capacity:100}")
    private int eventQueueCapacity;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    @Override
    public SseMessage save(SseMessage message) {
        makeAvailableCapacity();

        UUID eventId = message.getEventId();
        eventIdQueue.addLast(eventId);
        messages.put(eventId, message);

        log.info("[SseMessageRepository] 메시지 저장 - eventId: {}, eventName: {}",
                eventId, message.getEventName());

        return message;
    }

    @Override
    public List<SseMessage> findAllByEventIdAfterAndReceiverId(UUID eventId, UUID receiverId) {
        return eventIdQueue.stream()
                .dropWhile(id -> !id.equals(eventId))
                .skip(1)
                .map(messages::get)
                .filter(msg -> msg != null && msg.isReceivable(receiverId))
                .toList();
    }

    private void makeAvailableCapacity() {
        int availableCapacity = eventQueueCapacity - eventIdQueue.size();
        while (availableCapacity < 1) {
            UUID removed = eventIdQueue.removeFirst();
            messages.remove(removed);
            availableCapacity++;
        }
    }
}
