package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContentType;

public record AddBinaryContentDto(
        byte[] binaryContent,
        BinaryContentType contentType
) {
}
