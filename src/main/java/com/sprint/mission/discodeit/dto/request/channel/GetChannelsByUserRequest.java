package com.sprint.mission.discodeit.dto.request.channel;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetChannelsByUserRequest {
	@NotNull(message = "사용자 ID는 필수입니다")
	private UUID userId;
}
