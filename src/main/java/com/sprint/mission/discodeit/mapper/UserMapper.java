package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "userStatus", target = "online", qualifiedByName = "mapOnline")
    UserDto toDto(User user, UserStatus userStatus);

    @Named("mapOnline")
    default boolean mapOnline(UserStatus userStatus) {
        return userStatus != null && userStatus.isOnline();
    }
}
