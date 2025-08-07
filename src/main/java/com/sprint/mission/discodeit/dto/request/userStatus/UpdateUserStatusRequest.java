package com.sprint.mission.discodeit.dto.request.userStatus;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateUserStatusRequest {
	@NotBlank(message = "상태 ID는 필수")
	private UUID id;
}