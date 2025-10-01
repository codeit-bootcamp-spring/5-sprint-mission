package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>, MessageRepositoryCustom {
    List<Message> findByChannelId(UUID channelId);

    List<Message> findByAuthorIdAndChannelId(UUID authorId, UUID channelId);

    @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
    Optional<Instant> findLatestMessageTimeByChannelId(UUID channelId);

}
