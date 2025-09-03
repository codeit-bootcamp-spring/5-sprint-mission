package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(binaryContent.getId(), binaryContent.getFileName(),
            binaryContent.getSize(), binaryContent.getContentType(), binaryContent.getBytes());
    }
}
