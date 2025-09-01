package com.sprint.mission.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.domain.entity.UserStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserStatusMapper {

	public UserStatusDto toDto(UserStatus userStatus) {
		return UserStatusDto.builder()
		  .id(userStatus.getId())
		  .userId(userStatus.getUser().getId())
		  .lastActiveAt(userStatus.getLastActiveAt())
		  .build();
	}
}
