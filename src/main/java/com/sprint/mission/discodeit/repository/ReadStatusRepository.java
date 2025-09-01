package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatusDto> findAllByUserId(UUID userId);

    @Query("""
            SELECT rs.user
            FROM ReadStatus rs
            WHERE rs.channel = :channel
        """)
    List<User> findUsersByChannel(Channel channel);

    List<ReadStatus> findAllByChannelIn(Collection<Channel> channels);

    void deleteAllByChannel(Channel channel);

    default ReadStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ReadStatus with id %s not found".formatted(id))
        );
    }
}
