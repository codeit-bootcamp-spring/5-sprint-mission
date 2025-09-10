package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentDto.class)
public interface UserMapper {
    @Mapping(target = "online", expression = "java(user.getUserStatus().isOnline())")
    UserDto toDto(User user);
}
