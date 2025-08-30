package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    int deleteAllByUserId(UUID userId);

    default ReadStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ReadStatus with id %s not found".formatted(id))
        );
    }

    void deleteAllByChannel(Channel channel);
}
