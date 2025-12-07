package com.sprint.mission.discodeit.readstatus.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @EntityGraph(attributePaths = {"channel"})
    List<ReadStatus> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "user.profile"})
    List<ReadStatus> findAllByChannelIn(List<Channel> channels);

    @EntityGraph(attributePaths = {"user", "user.profile"})
    List<ReadStatus> findAllByChannelIdIn(List<UUID> channelIds);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    long deleteByChannelId(UUID channelId);

    long deleteByUserId(UUID userId);

    @Query("""
            SELECT rs FROM ReadStatus rs
            JOIN FETCH rs.user
            WHERE rs.channel.id = :channelId
              AND rs.notificationEnabled = true
              AND rs.user.id != :excludeUserId
        """)
    List<ReadStatus> findAllByChannelIdWithNotificationEnabled(UUID channelId, UUID excludeUserId);
}
