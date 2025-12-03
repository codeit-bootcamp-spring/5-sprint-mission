package com.sprint.mission.discodeit.event.auth;

import com.sprint.mission.discodeit.dto.user.data.UserDto;

public record TokenRefreshedEvent(
    UserDto user
) {
}
