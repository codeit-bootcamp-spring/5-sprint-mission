package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

	@Mapping(target = "online", source = "status.online")
	UserDto toDto(User user);
}
