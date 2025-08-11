package com.sprint.mission.discodeit.dto.request.userStatus;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateUserStatusRequest {
	@NotNull(message = "사용자 ID는 필수")
	private UUID userId;
}