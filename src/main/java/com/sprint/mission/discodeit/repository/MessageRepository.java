package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChannelId(UUID channelId);
    Slice<Message> findByChannelId(UUID channelId, Pageable pageable);
    void deleteAllByChannelId(UUID channelId);

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.channel
        JOIN FETCH m.author
        WHERE m.channel.id = :channelId
        ORDER BY m.createdAt DESC
        """)
    List<Message> findAllWithChannelAndAuthorByChannelId(@Param("channelId") UUID channelId, Pageable pageable);

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.channel
        JOIN FETCH m.author
        WHERE m.channel.id = :channelId
        AND m.createdAt < :cursor
        ORDER BY m.createdAt DESC
        """)
    List<Message> findTopNWithChannelAndAuthorByChannelId(
        @Param("channelId") UUID channelId,
        @Param("cursor") Instant cursor,
        Pageable pageable
    );
}

