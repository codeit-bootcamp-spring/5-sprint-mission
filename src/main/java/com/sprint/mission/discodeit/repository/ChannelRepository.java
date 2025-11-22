package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @Query("""
        SELECT c
        FROM Channel c
        LEFT JOIN ReadStatus rs ON rs.channel = c AND rs.user.id = :userId
        WHERE c.type = 'PUBLIC' OR rs.user.id = :userId
        ORDER BY CASE WHEN c.type = 'PRIVATE' THEN 0 ELSE 1 END
        """)
    List<Channel> findAllByUserId(@Param("userId") UUID userId);

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
