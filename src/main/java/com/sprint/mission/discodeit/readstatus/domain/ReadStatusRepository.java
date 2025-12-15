package com.sprint.mission.discodeit.readstatus.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"channel"})
    List<ReadStatus> findAllWithChannelByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "user.profile"})
    List<ReadStatus> findAllWithUserProfileByChannelIdIn(Collection<UUID> channelIds);

    @Query("""
            SELECT rs FROM ReadStatus rs
            JOIN FETCH rs.user
            WHERE rs.channel.id = :channelId
              AND rs.notificationEnabled = true
              AND rs.user.id != :excludeUserId
        """)
    List<ReadStatus> findNotificationTargets(UUID channelId, UUID excludeUserId);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    @Query("""
            SELECT rs.user.id
            FROM ReadStatus rs
            WHERE rs.channel.id = :channelId
        """)
    Set<UUID> findUserIdSetByChannelId(UUID channelId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM ReadStatus rs
            WHERE rs.channel.id = :channelId
        """)
    int deleteAllByChannelId(UUID channelId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM ReadStatus rs
            WHERE rs.user.id = :userId
        """)
    int deleteAllByUserId(UUID userId);
}
