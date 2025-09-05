package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    @Query("""
    SELECT rs FROM ReadStatus rs
    JOIN FETCH rs.user u
    LEFT JOIN FETCH u.profile
    WHERE u.id = :userId
    """)
    List<ReadStatus> findByUserId(UUID userId);
    List<ReadStatus> findByChannelId(UUID channelId);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    default ReadStatus findByChannelIdAndUserId(UUID channelId, UUID userId) {
        return findByUserIdAndChannelId(userId, channelId).orElse(null);
    }

    @Query("""
            SELECT u FROM ReadStatus rs
            JOIN rs.user u
            LEFT JOIN FETCH u.profile
            WHERE rs.channel.id = :channelId
            """)
    List<User> findUsersByChannelId(UUID channelId);
}
