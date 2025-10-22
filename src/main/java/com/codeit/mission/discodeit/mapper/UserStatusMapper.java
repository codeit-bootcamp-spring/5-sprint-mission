package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.UserStatusDto;
import com.codeit.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    @Mapping(target = "userId", source = "user.id")
    UserStatusDto toDto(UserStatus userStatus);
}
