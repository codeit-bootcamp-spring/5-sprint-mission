package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = BinaryContentMapper.class
)
public interface UserMapper {

    @Mapping(source = "userStatus", target = "online", qualifiedByName = "mapOnline")
    UserDto toDto(User user);

    @Named("userToDtoWithSince")
    @Mapping(source = "userStatus", target = "online", qualifiedByName = "mapOnlineWithSince")
    UserDto toDto(User user, @Context Instant onlineSince);

    @Named("mapOnline")
    default boolean mapOnline(UserStatus us) {
        return us != null && us.isOnline();
    }

    @Named("mapOnlineWithSince")
    default boolean mapOnline(UserStatus us, @Context Instant onlineSince) {
        return us != null && us.isOnline(onlineSince);
    }
}
