package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    default ReadStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ReadStatus with id %s not found".formatted(id))
        );
    }

    // Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    //
    // List<ReadStatus> findAllByUserId(UUID userId);
    //
    // List<ReadStatus> findAllByChannelId(UUID channelId);
    //
    // List<ReadStatus> findUnreadByUserId(UUID userId);
    //
    // long countUnreadByUserId(UUID userId);
    //
    // void deleteAllByUserId(UUID userId);
    //
    // void deleteAllByChannelId(UUID channelId);
}
