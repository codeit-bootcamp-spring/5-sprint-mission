package com.sprint.mission.discodeit.repository;

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
        SELECT c
        FROM Channel c
        LEFT JOIN ReadStatus rs ON rs.channel = c AND rs.user.id = :userId
        WHERE c.type = 'PUBLIC' OR rs.user.id = :userId
        """)
    List<Channel> findAllByUserId(@Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Channel> findForUpdateById(UUID id);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
                    FROM ReadStatus rs1
                    JOIN ReadStatus rs2 ON rs1.channel = rs2.channel
                    JOIN Channel c ON c = rs1.channel
                    WHERE c.type = 'PRIVATE'
                      AND rs1.user.id = :userId1
                      AND rs2.user.id = :userId2
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
