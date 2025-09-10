package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChannelId(UUID channelId);

    @Query("select m.createdAt from Message m where m.channel.id = :channelId order by m.createdAt desc limit 1")
    Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
