package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;

import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusResponse;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

	UserStatusResponse toResponse(UserStatusDto dto);
}