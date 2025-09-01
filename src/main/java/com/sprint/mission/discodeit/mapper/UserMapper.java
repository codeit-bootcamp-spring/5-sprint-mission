package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	private final BinaryContentMapper binaryContentMapper;
	private final UserService userService;

	public UserDto toDto(User user) {
		return UserDto.builder()
		  .id(user.getId())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profile(binaryContentMapper.toDto(user.getProfileImage()))
		  .online(userService.isOnline(user.getId()))
		  .build();
	}
}
