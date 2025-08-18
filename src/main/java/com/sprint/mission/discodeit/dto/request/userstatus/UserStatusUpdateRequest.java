package com.sprint.mission.discodeit.dto.request.userstatus;

import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(

    @NotNull
    UserStatusType userStatusType
) {

}
