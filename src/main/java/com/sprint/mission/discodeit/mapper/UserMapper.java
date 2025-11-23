package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {BinaryContentMapper.class, UserStatusMapper.class}
)
public interface UserMapper {

  @Mapping(
      target = "online",
      expression = "java(user.getStatus() != null && user.getStatus().isOnline())"
  )
    // role은 필드명이 같고 타입도 같아서 따로 @Mapping 안 써도 자동 매핑됨
  UserDto toDto(User user);
}
