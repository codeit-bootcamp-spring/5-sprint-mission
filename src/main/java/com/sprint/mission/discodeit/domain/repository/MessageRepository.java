package com.sprint.mission.discodeit.domain.repository;

import com.sprint.mission.discodeit.domain.dto.channel.data.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByChannelId(UUID channelId);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<Message> findByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.profile"})
    Page<Message> findByChannelIdAndCreatedAtBefore(UUID channelId, Instant createdAtBefore, Pageable pageable);

    Message findFirstByChannelOrderByCreatedAtDesc(Channel channel);

    @Query("""
        SELECT new com.sprint.mission.discodeit.domain.dto.channel.data.ChannelLastMessageAtDto(
            m.channel.id, MAX(m.createdAt)
        )
        FROM Message m
        WHERE m.channel IN :channels
        GROUP BY m.channel.id
        """)
    List<ChannelLastMessageAtDto> findLastMessageAtByChannels(@Param("channels") List<Channel> channels);

    @Modifying
    @Query("""
        UPDATE Message m
        SET m.author = null
        WHERE m.author.id = :userId
        """)
    long nullifyAuthorByUserId(@Param("userId") UUID userId);
}
