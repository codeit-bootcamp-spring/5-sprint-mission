package com.sprint.mission.discodeit.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	private final BinaryContentMapper binaryContentMapper;
	private final JwtRegistry<UUID> jwtRegistry;

	public UserDto toDto(User user) {
		boolean loggedIn = jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
		return UserDto.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.profile(binaryContentMapper.toDto(user.getProfile()))
			.online(loggedIn)
			.role(user.getRole())
			.build();
	}
}
