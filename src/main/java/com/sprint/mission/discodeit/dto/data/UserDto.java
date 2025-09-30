package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online
) {

}
