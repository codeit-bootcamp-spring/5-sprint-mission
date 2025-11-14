package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.CreateRequest;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

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

  public User toEntity(CreateCommand create, BinaryContent profile, String encodedPassword) {

    return User.builder()
               .username(create.getUsername())
               .email(create.getEmail())
               .password(encodedPassword == null ? create.getPassword() : encodedPassword)
               .profile(profile)
               .role(create.getRole() == null ? UserRole.USER.name() : create.getRole())
               .build();
  }

  public CreateCommand toCommand(CreateRequest request, MultipartFile profileImage) {
    return CreateCommand.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .profileImage(profileImage)
                        .role(UserRole.USER.name())
                        .build();
  }


  public UpdateCommand toCommand(UUID id, UpdateRequest request, MultipartFile profileImage) {
    return UpdateCommand.builder()
                        .id(id)
                        .username(request.getNewUsername())
                        .email(request.getNewEmail())
                        .password(request.getNewPassword())
                        .profileImage(profileImage)
                        .role(request.getNewRole() == null ? UserRole.USER.name()
                            : request.getNewRole())
                        .build();
  }
}
