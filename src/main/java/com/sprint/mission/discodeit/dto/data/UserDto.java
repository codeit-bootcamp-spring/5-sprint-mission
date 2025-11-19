package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(
    UUID id,
    String username,
    String email,
    UserRole role,
    BinaryContentDto profile,
    Boolean online
) {}
