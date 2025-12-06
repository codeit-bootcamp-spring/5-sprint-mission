package com.sprint.mission.discodeit.channel.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findAllByTypeOrIdIn(ChannelType type, Collection<UUID> ids);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
        FROM Channel c
        WHERE c.type = 'PRIVATE'
          AND EXISTS (
              SELECT rs FROM ReadStatus rs
              WHERE rs.channel = c AND rs.user.id = :userId1
          )
          AND EXISTS (
              SELECT rs FROM ReadStatus rs
              WHERE rs.channel = c AND rs.user.id = :userId2
          )
          AND (SELECT COUNT(rs3) FROM ReadStatus rs3 WHERE rs3.channel = c) = 2
        """)
    boolean existsBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}
