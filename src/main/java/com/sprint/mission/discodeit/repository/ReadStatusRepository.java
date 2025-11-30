package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatusDto> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user.profile"})
    @Query("""
            SELECT DISTINCT rs.user
            FROM ReadStatus rs
            WHERE rs.channel = :channel
        """)
    List<User> findUsersByChannel(Channel channel);

    List<ReadStatus> findAllByChannelIn(Collection<Channel> channels);

    int deleteAllByChannelId(UUID channelId);

    int deleteAllByUser(User user);

    Collection<Object> findAllByChannelId(UUID channelId);

    @Query("""
            SELECT rs FROM ReadStatus rs
            JOIN FETCH rs.user
            WHERE rs.channel.id = :channelId
              AND rs.notificationEnabled = true
              AND rs.user.id != :excludeUserId
        """)
    List<ReadStatus> findAllByChannelIdWithNotificationEnabled(UUID channelId, UUID excludeUserId);
}
