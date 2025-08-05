package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;

public record AuthLoginResponse(
        User user
) {
}
