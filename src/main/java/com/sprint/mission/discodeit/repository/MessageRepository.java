package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    default Message getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Message with id %s not found".formatted(id))
        );
    }

    // List<Message> findAllByChannelId(UUID channelId);
    //
    // List<Message> findAllByAuthorId(UUID authorId);
    //
    // void deleteAllByChannelId(UUID channelId);
    //
    // void deleteAllByAuthorId(UUID authorId);
    //
    // long countByChannelId(UUID channelId);
}
