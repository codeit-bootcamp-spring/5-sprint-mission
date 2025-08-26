package com.sprint.mission.discodeit.dto.request.user;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserEmailRequest {
	@NotNull
	private UUID id;
	@NotBlank(message = "이메일 필수")
	private String email;
}
