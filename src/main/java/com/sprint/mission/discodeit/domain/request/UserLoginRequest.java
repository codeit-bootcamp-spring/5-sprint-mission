package com.sprint.mission.discodeit.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserLoginRequest {
	@NotBlank(message = "username은 비어있을 수 없습니다.")
	private String username;
	@NotBlank(message = "password은 비어있을 수 없습니다.")
	private String password;
}
