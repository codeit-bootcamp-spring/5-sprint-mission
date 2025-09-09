package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {
    BinaryContentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

  @Autowired
  protected BinaryContentMapper binaryContentMapper;

  @Mapping(target = "profile", expression = "java(binaryContentMapper.toDetail(user.getProfile()))")
  @Mapping(target = "online", expression = "java(user.getStatus() != null && user.getStatus().isOnline())")
  public abstract UserDto.Detail toDetail(User user);

  @Mapping(target = "profile", expression = "java(binaryContentMapper.toDetailResponse(detail.getProfile()))")
  public abstract UserDto.DetailResponse toDetailResponse(UserDto.Detail detail);

  public User toEntity(CreateCommand create, BinaryContent profile) {
    return User.builder()
               .username(create.getUsername())
               .email(create.getEmail())
               .password(create.getPassword())
               .profile(profile)
               .build();
  }
}
