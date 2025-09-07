package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.sub.BinaryContent;

import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        Long size,
        String contentType,
        byte[] bytes
) {

    public static BinaryContentDto from(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getBytes()
        );
    }
}
