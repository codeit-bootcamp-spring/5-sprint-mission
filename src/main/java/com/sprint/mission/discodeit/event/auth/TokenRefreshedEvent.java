package com.sprint.mission.discodeit.event.auth;

import com.sprint.mission.discodeit.dto.auth.data.UserDetailsDto;

public record TokenRefreshedEvent(
    UserDetailsDto userDetailsDto
) {
}
