package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContent;

public record BinaryContentCreateRequest(
        String fileName,
        BinaryContent.ContentType contentType,
        byte[] bytes) {
}
