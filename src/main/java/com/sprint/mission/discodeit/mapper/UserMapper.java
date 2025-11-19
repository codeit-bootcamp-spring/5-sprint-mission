package com.sprint.mission.discodeit.mapper;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	private final BinaryContentMapper binaryContentMapper;
	private final SessionRegistry sessionRegistry;

	public UserDto toDto(User user) {
		boolean loggedIn = sessionRegistry.getAllPrincipals().stream()
			.anyMatch(principal -> ((DiscodeitUserDetails)principal).getUserDto().id().equals(user.getId()));
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
