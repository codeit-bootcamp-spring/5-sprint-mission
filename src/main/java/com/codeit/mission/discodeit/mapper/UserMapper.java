package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.entity.UserStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        BinaryContentDto profile = Optional.ofNullable(user.getProfile())
            .map(binaryContentMapper::toDto)
            .orElse(null);

        Boolean online = Optional.ofNullable(user.getStatus())
            .map(UserStatus::isOnline)
            .orElse(null);

        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), profile, online);
    }
}
