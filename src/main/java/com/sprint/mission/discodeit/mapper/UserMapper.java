package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.dto.user.UserResponse;
import com.sprint.mission.discodeit.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	private final BinaryContentMapper binaryContentMapper;

	public UserDto toDto(User user, boolean isOnline) {
		return UserDto.builder()
		  .id(user.getId())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profile(binaryContentMapper.toDto(user.getProfileImage()))
		  .online(isOnline)
		  .build();
	}

	public UserResponse toResponse(UserDto userDto) {
		return UserResponse.builder()
		  .id(userDto.getId())
		  .username(userDto.getUsername())
		  .email(userDto.getEmail())
		  .profile(userDto.getProfile())
		  .online(userDto.getOnline())
		  .build();
	}
}
