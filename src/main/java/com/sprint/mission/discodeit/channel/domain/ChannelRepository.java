package com.sprint.mission.discodeit.channel.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findAllByType(ChannelType type);

    @Query("""
        SELECT COUNT(rs1) > 0
        FROM ReadStatus rs1
        JOIN ReadStatus rs2 ON rs1.channel = rs2.channel
        JOIN Channel c ON rs1.channel = c
        WHERE rs1.user.id = :userId1
          AND rs2.user.id = :userId2
          AND c.type = 'PRIVATE'
          AND (SELECT COUNT(rs3) FROM ReadStatus rs3 WHERE rs3.channel = c) = 2
        """)
    boolean existsBetweenUsers(UUID userId1, UUID userId2);
}
