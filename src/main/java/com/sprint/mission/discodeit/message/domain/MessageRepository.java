package com.sprint.mission.discodeit.message.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<Message> findPagedWithAuthorAndProfileByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<Message> findPagedWithAuthorAndProfileByChannelIdAndCreatedAtBefore(UUID channelId, Instant createdAtBefore, Pageable pageable);

    @Query("""
        SELECT m.id
        FROM Message m
        WHERE m.channel.id = :channelId
        """)
    Set<UUID> findAllIdsByChannelId(UUID channelId);

    @Query("""
        SELECT MAX(m.createdAt)
        FROM Message m
        WHERE m.channel.id = :channelId
        """)
    Optional<Instant> findLastCreatedAtByChannelId(UUID channelId);

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
    List<Message> findLastMessageByChannelIdIn(List<UUID> channelIds);

    @Modifying
    @Query("""
        UPDATE Message m
        SET m.author = null
        WHERE m.author.id = :userId
        """)
    int nullifyAuthorByUserId(UUID userId);

    long deleteByChannelId(UUID channelId);
}
