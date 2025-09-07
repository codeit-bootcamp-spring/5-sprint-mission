package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;

    public UserMapper(BinaryContentMapper binaryContentMapper) {
        this.binaryContentMapper = binaryContentMapper;
    }

    public UserDto toDto(User user, Boolean online) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getProfile() != null ? binaryContentMapper.toDto(user.getProfile()) : null,
            online
        );
    }
}
