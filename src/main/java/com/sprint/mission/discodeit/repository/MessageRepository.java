package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChannelId(UUID channelId);
    List<Message> findByAuthorIdAndChannelId(UUID authorId, UUID channelId);

    @Query("""
    select m
    from Message m
    where m.channel.id = :channelId
    order by m.createdAt desc
    """)
    Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

    @Query("""
    select m
    from Message m
    where m.channel.id = :channelId
    order by m.createdAt desc
    """)
    Page<Message> findPageByChannelId(UUID channelId, Pageable pageable);

    @Query("""
    SELECT m FROM Message m
    JOIN FETCH m.author a
    LEFT JOIN FETCH a.profile
    LEFT JOIN FETCH a.userStatus
    LEFT JOIN FETCH m.attachments
    WHERE m.channel.id = :channelId
    ORDER BY m.createdAt DESC
    """)
    Slice<Message> findSliceByChannelId(UUID channelId, Pageable pageable);

    @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
    Optional<Instant> findLatestMessageTimeByChannelId(UUID channelId);

}
