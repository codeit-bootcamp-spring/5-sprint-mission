package com.sprint.mission.discodeit.dto.request.channel;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetChannelsByUserRequest {
	@NotBlank(message = "사용자 ID는 필수입니다")
	private UUID userId;
}
