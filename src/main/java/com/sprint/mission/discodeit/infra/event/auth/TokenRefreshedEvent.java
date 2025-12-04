package com.sprint.mission.discodeit.infra.event.auth;

import com.sprint.mission.discodeit.domain.dto.auth.data.UserDetailsDto;

public record TokenRefreshedEvent(
    UserDetailsDto userDetailsDto
) {
}
