package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelParticipant;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelParticipantRepository extends JpaRepository<ChannelParticipant, UUID> {

    default ChannelParticipant getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ChannelParticipant with id %s not found".formatted(id))
        );
    }
}
