package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.main.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);
    void deleteAllByChannelId(UUID channelId);
}