package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
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
