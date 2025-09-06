package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    Optional<Message> findLatestByChannelId(UUID channelId);
    List<Message> findAllByChannelId(UUID channelId);
    void delete(UUID id);
    void deleteAllByChannelId(UUID channelId);
}
