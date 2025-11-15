package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.log.LogUtils;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

	@Size(min = 4, max = 50, message = "ID는 최소 4자부터 최대 50자까지 가능합니다")
	String newUsername,

	@Email
	@Size(max = 100, message = "email은 최대 100자 까지 가능합니다")
	String newEmail,

	@Size(min = 8, max = 20, message = "비밀번호는 최소 8자부터 최대 20자까지 가능합니다")
	String newPassword
) {

	public String forLog() {
		return "UserCreateRequest{" +
			"username=" + newUsername +
			", email=" + LogUtils.maskEmail(newEmail) +
			"}";
	}

}
