package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {
;
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);
    void deleteAllByChannelId(UUID channelId);
}
