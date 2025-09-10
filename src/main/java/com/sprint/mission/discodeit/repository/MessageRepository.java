package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    void deleteAllByChannelId(UUID channelId);

    @Query("SELECT m.createAt " +
            "from Message m " +
            "where m.channel.id = :channelId " +
            " order by m.createAt desc")
    Instant findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

    List<Message> findAllByChannelId(UUID id);
}
