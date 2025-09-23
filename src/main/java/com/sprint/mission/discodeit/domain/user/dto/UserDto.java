package com.sprint.mission.discodeit.domain.user.dto;

import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online
) {}
