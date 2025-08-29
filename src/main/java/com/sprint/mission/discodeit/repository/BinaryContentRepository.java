package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    @Modifying
    @Query("DELETE FROM BinaryContent b WHERE b.id = :id")
    int deleteIfExists(@Param("id") UUID id);

    default BinaryContent getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "BinaryContent with id %s not found".formatted(id))
        );
    }
}
