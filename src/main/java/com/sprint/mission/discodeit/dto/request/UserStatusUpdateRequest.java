package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.domain.enums.UserStatusType;

public record UserStatusUpdateRequest(
        UserStatusType userStatusType
) {
}
