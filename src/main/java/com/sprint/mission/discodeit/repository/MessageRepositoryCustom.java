package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepositoryCustom {
    List<Message> findByChannelIdWithCursor(UUID channelId, Instant cursor, int size);
}