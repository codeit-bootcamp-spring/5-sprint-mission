package com.sprint.mission.discodeit.dto.request;

import lombok.Builder;

@Builder
public record UserLoginRequest(
        String username,
        String password
) {
}
