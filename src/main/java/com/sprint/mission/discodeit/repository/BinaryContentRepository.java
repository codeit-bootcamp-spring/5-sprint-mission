package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    List<BinaryContentDto> findAllByIdIn(Collection<UUID> ids);

    @Query("SELECT b.bytes FROM BinaryContent b WHERE b.id = :id")
    byte[] findBytesById(@Param("id") UUID id);

    default BinaryContent getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "BinaryContent with id %s not found".formatted(id))
        );
    }
}
