package com.sprint.mission.discodeit.dto.request.userstatus;

import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import java.time.Instant;

public record UserStatusUpdateRequest(

    UserStatusType newUserStatusType,
    Instant newLastActiveAt
) {

}
