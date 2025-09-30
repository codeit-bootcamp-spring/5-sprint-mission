package com.sprint.mission.discodeit.domain.user.mapper;

import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.domain.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.domain.userstatus.mapper.UserStatusMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

  @Mapping(target = "online", expression = "java(user.getStatus().isOnline())")
  UserDto toDto(User user);
}
