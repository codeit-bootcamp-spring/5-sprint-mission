package com.sprint.mission.discodeit.dto.request.readStatus;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateReadStatusRequest {
	@NotNull
	private UUID id;
}
