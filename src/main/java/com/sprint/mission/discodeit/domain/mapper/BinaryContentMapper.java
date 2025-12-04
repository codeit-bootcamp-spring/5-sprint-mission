package com.sprint.mission.discodeit.domain.mapper;

import com.sprint.mission.discodeit.domain.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import org.springframework.stereotype.Component;

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
            entity.getContentType(),
            entity.getStatus()
        );
    }
}
