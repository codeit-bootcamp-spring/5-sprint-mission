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
public class UpdateUserStatusRequest {
	@NotNull(message = "상태 ID는 필수")
	private UUID id;
}