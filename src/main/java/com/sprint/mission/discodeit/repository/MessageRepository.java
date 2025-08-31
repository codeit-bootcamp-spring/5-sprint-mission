package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channel.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

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
        WHERE m.channel = :channel
        """
    )
    Instant findLastMessageAtByChannel(@Param("channel") Channel channel);

    default Message getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Message with id %s not found".formatted(id))
        );
    }

    void deleteAllByChannel(Channel channel);
}
