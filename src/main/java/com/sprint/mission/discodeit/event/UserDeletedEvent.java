package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDeletedEvent {
    private final UserResponse userResponse;
}