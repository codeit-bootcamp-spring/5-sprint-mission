package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType
) {

    public static BinaryContentDto from(BinaryContent bc) {
        return new BinaryContentDto(
            bc.getId(),
            bc.getFileName(),
            bc.getSize(),
            bc.getContentType()
        );
    }
}
