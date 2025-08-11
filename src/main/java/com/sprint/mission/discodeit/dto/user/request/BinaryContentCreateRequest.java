package com.sprint.mission.discodeit.dto.user.request;

import java.time.Instant;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {}
