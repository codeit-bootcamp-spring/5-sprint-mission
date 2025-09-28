package com.sprint.mission.discodeit.mapper;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

  @Mapping(target = "online", expression = "java(user.getStatus().isOnline())")
  UserDto toDto(User user);

  Object toUser(Object any);

//  Dto
//  UUID id,
//  String username,
//  String email,
//  BinaryContentDto profile,
//  Boolean online
//
//  User
//  private String username;
//  private String email;
//  private String password;
//  private BinaryContent profile;
//  private UserStatus status;


  @Mapping(target="password", ignore = true)
  @Mapping(target="profile", ignore = true)
  @Mapping(target="status", ignore = true)
  User toUser(UserDto userDto);
}
