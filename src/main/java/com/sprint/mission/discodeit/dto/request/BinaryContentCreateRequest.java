package com.sprint.mission.discodeit.dto.request;

import java.util.*;

public record BinaryContentCreateRequest (
        UUID binaryContentId,
        List<UUID> attachmentIds,
        UUID userId,
        String fileName,
        String contentType,
        Long size,
        byte[] bytes
){
}
