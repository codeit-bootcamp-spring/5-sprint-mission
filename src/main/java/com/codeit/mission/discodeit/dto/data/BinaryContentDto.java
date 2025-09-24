package com.codeit.mission.discodeit.dto.data;

import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        Long size,
        String contentType
) {

}
