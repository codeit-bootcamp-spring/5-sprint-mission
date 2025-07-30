package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);
    Optional<Message> findById (UUID id);
    List<Message> findAll ();
    List<Message> findByChannelId (UUID channelId);
    void update(UUID id, String newContent);
    void delete(UUID id);
}
