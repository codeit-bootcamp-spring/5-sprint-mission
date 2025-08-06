package com.sprint.mission.discodeit.dto.request.message;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
public class GetMessageRequest {
	@NotNull(message = "메시지 ID는 필수")
	private UUID messageId;
}