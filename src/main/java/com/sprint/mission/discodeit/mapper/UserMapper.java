package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final JwtRegistry jwtRegistry;
    private final BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            binaryContentMapper.toDto(user.getProfile()),
            jwtRegistry.hasActiveJwtInformationByUserId(user.getId()),
            user.getRole()
        );
    }
}
