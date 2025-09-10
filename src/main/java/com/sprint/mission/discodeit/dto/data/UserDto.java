package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Boolean online
) {
}
