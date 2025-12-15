package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentMapper;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;

    private final JwtRegistry jwtRegistry;

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

    public List<UserDto> toDtoList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        Set<UUID> activeUserIds = jwtRegistry.getActiveUserIds();
        return users.stream()
            .map(user -> toDto(user, activeUserIds.contains(user.getId())))
            .toList();
    }

    private UserDto toDto(User user, boolean hasActiveJwt) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            binaryContentMapper.toDto(user.getProfile()),
            hasActiveJwt,
            user.getRole()
        );
    }
}
