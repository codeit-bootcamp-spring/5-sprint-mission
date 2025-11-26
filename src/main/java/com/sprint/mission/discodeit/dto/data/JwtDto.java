package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {
}
