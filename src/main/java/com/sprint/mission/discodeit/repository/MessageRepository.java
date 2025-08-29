package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Message m set m.author = null where m.author.id = :userId")
    int nullifyAuthorByUserId(@Param("userId") UUID userId);

    default Message getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "Message with id %s not found".formatted(id))
        );
    }
}
