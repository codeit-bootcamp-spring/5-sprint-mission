package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
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
}
