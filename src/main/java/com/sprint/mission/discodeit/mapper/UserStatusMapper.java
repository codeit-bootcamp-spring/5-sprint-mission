package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  public abstract UserStatusDto.Detail toDetail(UserStatus userStatus);

  public abstract UserStatusDto.DetailResponse toDetailResponse(UserStatusDto.Detail detail);
}
