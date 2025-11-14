package com.sprint.mission.discodeit.domain.dto.user;

import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserDto {
	private UUID id;
	private String username;
	private String email;
	private BinaryContentDto profile;
	private Boolean online;
	private Role role;

	public static UserDto of(User user, BinaryContentDto profile, Boolean online) {
		return new UserDto(user.getId(), user.getUsername(), user.getEmail(), profile, online, user.getRole());
	}
}
