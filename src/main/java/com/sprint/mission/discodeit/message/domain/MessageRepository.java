package com.sprint.mission.discodeit.message.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"author", "channel"})
    Optional<Message> findWithAuthorAndChannelById(UUID id);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Slice<Message> findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
        UUID channelId, Instant cursor, Pageable pageable);

    @Query("""
        SELECT m.id
        FROM Message m
        WHERE m.channel.id = :channelId
        """)
    Set<UUID> findIdSetByChannelId(UUID channelId);

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
    List<Message> findLastMessageByChannelIdIn(Collection<UUID> channelIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Message m
        SET m.author = null
        WHERE m.author.id = :userId
        """)
    int nullifyAuthorByUserId(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Message m
        WHERE m.channel.id = :channelId
        """)
    int deleteAllByChannelId(UUID channelId);

    boolean existsByIdAndAuthorId(UUID id, UUID authorId);
}
