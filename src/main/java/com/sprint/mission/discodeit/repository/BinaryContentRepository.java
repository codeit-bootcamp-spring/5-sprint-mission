package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    List<BinaryContentDto> findAllByIdIn(Collection<UUID> ids);

    Optional<BinaryContentDto> findToDtoById(UUID id);

    default BinaryContentDto getOrThrowToDto(UUID id) {
        return findToDtoById(id).orElseThrow(() ->
            new NotFoundException(
                "BinaryContent with id %s not found".formatted(id))
        );
    }
}
