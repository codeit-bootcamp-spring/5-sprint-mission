package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChannelId(UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
