package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.sse.SseMessage;

import java.util.List;
import java.util.UUID;

public interface SseMessageRepository {
    SseMessage save(SseMessage message);

    List<SseMessage> findAllByEventIdAfterAndReceiverId(UUID eventId, UUID receiverId);
}
