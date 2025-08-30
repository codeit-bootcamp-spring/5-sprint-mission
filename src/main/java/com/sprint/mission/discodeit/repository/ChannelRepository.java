package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.NotFoundException;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @Query("""
        SELECT new com.sprint.mission.discodeit.dto.channel.ChannelDto(
            c.id,
            c.type,
            c.name,
            c.description,
            NULL,
            (SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = c.id)
        )
        FROM Channel c
        LEFT JOIN ChannelParticipant cp ON cp.channelId = c.id AND cp.userId = :userId
        WHERE c.type = 'PUBLIC' OR cp.userId = :userId
        """)
    List<ChannelDto> findAllWithoutParticipantsByUserId(@Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Channel> findForUpdateById(UUID id);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
                    FROM ChannelParticipant cp1
                    JOIN ChannelParticipant cp2 ON cp1.channelId = cp2.channelId
                    JOIN Channel c ON c.id = cp1.channelId
                    WHERE c.type = 'PRIVATE'
                      AND cp1.userId = :userId1
                      AND cp2.userId = :userId2
        """)
    boolean existsBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    default Channel getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Channel with id %s not found".formatted(id))
        );
    }

    default Channel getOrThrowForUpdate(UUID id) {
        return findForUpdateById(id).orElseThrow(() ->
            new NotFoundException(
                "Channel with id %s not found".formatted(id))
        );
    }
}
