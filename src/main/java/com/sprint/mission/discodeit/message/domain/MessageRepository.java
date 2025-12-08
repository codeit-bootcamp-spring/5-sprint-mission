package com.sprint.mission.discodeit.message.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        SELECT m.id
        FROM Message m
        WHERE m.channel.id = :channelId
        """)
    Set<UUID> findIdsByChannelId(UUID channelId);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<Message> findByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<Message> findByChannelIdAndCreatedAtBefore(UUID channelId, Instant createdAtBefore, Pageable pageable);

    Message findFirstByChannelOrderByCreatedAtDesc(Channel channel);

    @Query("""
        SELECT m
        FROM Message m
        WHERE m.channel.id IN :channelIds
        AND m.createdAt = (
            SELECT MAX(m2.createdAt)
            FROM Message m2
            WHERE m2.channel = m.channel
        )
        """)
    List<Message> findLastMessageByChannelIdIn(@Param("channelIds") List<UUID> channelIds);

    @Modifying
    @Query("""
        DELETE FROM Message m
        WHERE m.channel.id = :channelId
        """)
    long deleteAllByChannelId(UUID channelId);

    @Modifying
    @Query("""
        UPDATE Message m
        SET m.author = null
        WHERE m.author.id = :userId
        """)
    int nullifyAuthorByUserId(@Param("userId") UUID userId);
}
