package com.sprint.mission.discodeit.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class UserCreateRequest {
	@NotBlank(message = "username은 비어있을 수 없습니다.")
	private final String username;
	@NotBlank(message = "email은 비어있을 수 없습니다.")
	private final String email;
	@NotBlank(message = "password은 비어있을 수 없습니다.")
	private final String password;
}
