package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent content) {
        return new BinaryContentDto(
            content.getId(),
            content.getFileName(),
            content.getSize(),
            content.getContentType()
        );
    }
}
