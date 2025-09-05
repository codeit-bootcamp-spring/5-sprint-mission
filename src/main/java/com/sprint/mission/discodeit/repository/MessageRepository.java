package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channel.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(
        attributePaths = {
            "author", "author.profile", "author.userStatus"
        }
    )
    Page<Message> findPageWithoutCursorByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(
        attributePaths = {
            "author", "author.profile", "author.userStatus"
        }
    )
    @Query("""
        SELECT m
        FROM Message m
        WHERE m.channel.id = :channelId AND m.createdAt < :cursor
        """
    )
    Page<Message> findPageByChannelId(
        @Param("channelId") UUID channelId,
        @Param("cursor") Instant cursor,
        Pageable pageable
    );

    @Query("""
        SELECT new com.sprint.mission.discodeit.dto.channel.ChannelLastMessageAtDto(
            m.channel.id,
            MAX(m.createdAt)
        )
        FROM Message m
        WHERE m.channel IN :channels
        GROUP BY m.channel.id
        """
    )
    List<ChannelLastMessageAtDto> findLastMessageAtByChannels(
        @Param("channels") Collection<Channel> channels
    );

    @Query("""
        SELECT MAX(m.createdAt)
        FROM Message m
        WHERE m.channel.id = :channelId
        """
    )
    Instant findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

    @Modifying
    @Query("""
        UPDATE Message m
        SET m.author = null
        WHERE m.author = :user
        """)
    int nullifyAuthorByUser(@Param("user") User user);

    @Modifying
    @Query(
        value = """
            DELETE FROM binary_contents bc
            WHERE bc.id IN (
                SELECT ma.attachment_id
                FROM message_attachments ma
                WHERE ma.channel_id = :channelId
            );
            DELETE FROM message_attachments ma
            WHERE ma.message_id IN (
                SELECT id
                FROM messages
                WHERE channel_id = :channelId
            );
            DELETE FROM messages WHERE channel_id = :channelId;
            """,
        nativeQuery = true
    )
    int deleteAllByChannelId(@Param("channelId") UUID channelId);

    default Message getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Message with id %s not found".formatted(id))
        );
    }
}
