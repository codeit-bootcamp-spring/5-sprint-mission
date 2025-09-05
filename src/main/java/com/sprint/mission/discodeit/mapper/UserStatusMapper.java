package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

	@Mapping(source = "userStatus.id", target = "id")
	@Mapping(source = "user.id", target = "userId")
	UserStatusDto toDto(UserStatus userStatus, User user);

	UserStatusResponse toResponse(UserStatusDto dto);
}
