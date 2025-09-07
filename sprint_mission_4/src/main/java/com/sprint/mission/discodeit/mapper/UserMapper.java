package com.sprint.mission.discodeit.mapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

//  @Mapping(target = "password", ignore = true)
//  @Mapping(target = "id", ignore = true)
  @Mapping(target="Online", expression = "java(user.getUserStatus().isOnline(), userStatusRepository))")
  UserDto toDto(User user);
}