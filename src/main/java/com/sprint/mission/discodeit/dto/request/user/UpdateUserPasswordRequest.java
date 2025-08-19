package com.sprint.mission.discodeit.dto.request.user;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserPasswordRequest {
	private UUID id;
	@NotBlank(message = "비밀번호 필수")
	private String currentPassword;
	private String newPassword;
}
