package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.domain.enums.user.Status;

public record UserStatusUpdateRequest(
        Status status
) {
}
