package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class,
        UserStatusMapper.class})
public interface UserMapper {

    @Mapping(target = "online", expression = "java(user.getStatus().isOnline())")
    UserDto toDto(User user);
}
