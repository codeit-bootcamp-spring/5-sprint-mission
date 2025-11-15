package com.sprint.mission.discodeit.dto.neutral;

import java.util.Optional;

import com.sprint.mission.discodeit.log.LogUtils;

public record UserCommand(
	String username,
	String email,
	String password,
	Optional<NewBinaryContent> profile
) {

	public String forLog() {
		return "UserCommand{" +
			"username=" + username +
			", email=" + LogUtils.maskEmail(email) +
			", profile" + (profile.isPresent() ? profile.get().forLog() : Optional.empty());
	}

}
