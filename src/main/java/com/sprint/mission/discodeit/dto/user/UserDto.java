package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    boolean online
) {

    public UserDto(
        UUID id,
        String username,
        String email,
        BinaryContent profile,
        boolean online
    ) {
        this(id, username, email, BinaryContentDto.from(profile), online);
    }

    public static UserDto from(User user, UserStatus userStatus) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getProfile() != null ? BinaryContentDto.from(user.getProfile()) : null,
            userStatus.isOnline()
        );
    }
}
