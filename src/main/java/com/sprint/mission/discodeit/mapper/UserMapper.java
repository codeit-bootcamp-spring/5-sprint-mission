package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.main.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "readStatuses", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "profile", source = "profile")
    @Mapping(
            target = "online",
            expression = "java(user.getStatus() != null && " +
                    "user.getStatus().getLastActiveAt() != null && " +
                    "java.time.Duration.between(user.getStatus().getLastActiveAt(), java.time.Instant.now()).toMinutes() < 5)"
    )
    UserDto toDto(User user);
}
