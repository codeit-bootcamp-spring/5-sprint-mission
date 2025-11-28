package com.sprint.mission.discodeit.domain.dto.response;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;

public record JwtResponse(UserDto userDto, String accToken) {
}
