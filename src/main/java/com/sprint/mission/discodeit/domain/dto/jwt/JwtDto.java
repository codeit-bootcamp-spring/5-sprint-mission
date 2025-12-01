package com.sprint.mission.discodeit.domain.dto.jwt;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;

public record JwtDto(UserDto userDto, String accToken) {
}
