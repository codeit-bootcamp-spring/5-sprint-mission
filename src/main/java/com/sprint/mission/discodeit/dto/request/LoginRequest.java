package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank(message = "ID를 입력 해 주세요")
	String username,

	@NotBlank(message = "비밀번호를 입력 해 주세요")
	String password
) {

}
