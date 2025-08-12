package com.sprint.mission.discodeit.dto.message.request;

import java.util.UUID;

public record MessageUpdateDto(
        UUID id,
        String content
) {
}
