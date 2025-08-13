package com.sprint.mission.discodeit.dto.request.readStatus;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateReadStatusRequest {
	@NotNull
	private UUID userId;
	@NotNull
	private UUID channelId;
}
