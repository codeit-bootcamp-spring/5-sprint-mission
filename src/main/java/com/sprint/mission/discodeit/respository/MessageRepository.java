package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);

    List<Message> findAll();

    Optional<Message> findById(UUID id);

    List<Message> findByContent(String str);

    Optional<Instant>  findLastCreatedAtByChannelId(UUID channelId);

    boolean deleteById(UUID id);
}
