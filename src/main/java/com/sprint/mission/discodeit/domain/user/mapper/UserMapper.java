package com.sprint.mission.discodeit.domain.user.mapper;

import com.sprint.mission.discodeit.domain.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.domain.user.dto.data.UserDto;
import com.sprint.mission.discodeit.domain.user.entity.User;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
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
