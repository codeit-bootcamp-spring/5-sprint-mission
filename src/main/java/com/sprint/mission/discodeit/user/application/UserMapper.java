package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentMapper;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
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
