package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    @Query("SELECT b.id from BinaryContent b")
    Set<UUID> findAllIds();

    List<BinaryContentDto> findAllByIdIn(Collection<UUID> ids);

    Optional<BinaryContentDto> findToDtoById(UUID id);

    default BinaryContentDto getOrThrowToDto(UUID id) {
        return findToDtoById(id).orElseThrow(() ->
            new NotFoundException(
                "BinaryContent with id %s not found".formatted(id))
        );
    }
}
