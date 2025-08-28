package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    default BinaryContent getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "BinaryContent with id %s not found".formatted(id))
        );
    }
}
