package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(target = "profile", source = "profile")
  @Mapping(target = "online", expression = "java(toOnline(user.getStatus()))")
  UserDto toDto(User user);

  default Boolean toOnline(UserStatus userStatus) {
    return userStatus != null ? userStatus.isOnline() : null;
  }
}
