package com.sprint.mission.discodeit.dto.request.readStatus;

import java.time.Instant;
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
	private Instant newlastReadAt;
}
