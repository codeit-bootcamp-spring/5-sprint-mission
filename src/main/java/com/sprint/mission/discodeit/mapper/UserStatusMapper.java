package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserStatusMapper {

    public static UserStatusResponse toUserStatusResponse(UserStatus userStatus) {
        Objects.requireNonNull(userStatus, "userStatus must not be null");
        return new UserStatusResponse(
                userStatus.getUserId(),
                userStatus.getType().getDisplayName()
        );
    }
}
