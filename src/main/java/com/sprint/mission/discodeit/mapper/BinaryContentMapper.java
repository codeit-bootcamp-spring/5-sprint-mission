package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent entity) {
        if (entity == null) {
            return null;
        }

        return new BinaryContentDto(
            entity.getId(),
            entity.getFileName(),
            entity.getSize(),
            entity.getContentType()
        );
    }

    public List<BinaryContentDto> toDtoList(List<BinaryContent> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
            .map(this::toDto)
            .toList();
    }
}
