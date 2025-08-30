package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channelparticipants.ChannelParticipantRow;
import com.sprint.mission.discodeit.entity.ChannelParticipant;
import com.sprint.mission.discodeit.entity.ChannelParticipantId;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelParticipantRepository extends
    JpaRepository<ChannelParticipant, ChannelParticipantId> {

    @Query("""
        SELECT new com.sprint.mission.discodeit.dto.channelparticipants.ChannelParticipantRow(
            cp.channelId,
            new com.sprint.mission.discodeit.dto.user.UserDto(
                u.id,
                u.username,
                u.email,
                p.id,
                p.fileName,
                p.size,
                p.contentType,
                CASE WHEN us.lastActiveAt IS NOT NULL AND us.lastActiveAt >= :onlineSince
                     THEN TRUE ELSE FALSE END
            )
        )
        FROM ChannelParticipant cp
        JOIN User u ON u.id = cp.userId
        JOIN UserStatus us ON us.user = u
        LEFT JOIN u.profile p
        WHERE cp.channelId IN :channelIds
        """
    )
    List<ChannelParticipantRow> findParticipantsByChannelIds(
        @Param("channelIds") Collection<UUID> channelIds,
        @Param("onlineSince") Instant onlineSince
    );

    int deleteAllByUserId(UUID userId);

    default ChannelParticipant getOrThrow(ChannelParticipantId id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ChannelParticipant with id %s not found".formatted(id))
        );
    }

    void deleteAllByChannelId(UUID channelId);
}
