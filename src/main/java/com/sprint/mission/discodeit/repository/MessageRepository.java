package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Modifying
    @Query("UPDATE Message m SET m.author = NULL WHERE m.author.id = :userId")
    int nullifyAllAuthorByUserId(UUID userId);

    default Message getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Message with id %s not found".formatted(id))
        );
    }
}
