package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.log.LogUtils;

import lombok.Builder;

@Builder
public record UserDto(
	UUID id,
	String username,
	String email,
	BinaryContentDto profile,
	Boolean online,
	Role role
) {

	public String forLog() {
		return "UserDto{" +
			"id=" + id +
			", username=" + username +
			", email=" + LogUtils.maskEmail(email) +
			", profile=" + LogUtils.summarizeAttachment(profile) +
			", online=" + online +
			", role=" + role +
			"}";
	}

}
