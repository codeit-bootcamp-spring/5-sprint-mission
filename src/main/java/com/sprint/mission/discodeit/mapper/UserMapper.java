package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserResponse toUserResponse(User user, Status status) {
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmail(),
                user.getUsername(),
                user.getGlobalName(),
                user.getProfileId(),
                status
        );
    }
}
