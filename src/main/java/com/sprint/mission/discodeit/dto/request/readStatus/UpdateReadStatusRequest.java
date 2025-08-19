package com.sprint.mission.discodeit.dto.request.readStatus;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateReadStatusRequest {
	@NotBlank
	private UUID id;
}
