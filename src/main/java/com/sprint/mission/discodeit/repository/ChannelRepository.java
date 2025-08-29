package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    default Channel getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Channel with id %s not found".formatted(id))
        );
    }
}
