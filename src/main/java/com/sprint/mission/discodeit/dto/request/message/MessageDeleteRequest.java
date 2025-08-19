package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MessageDeleteRequest {
	@NotNull(message = "메시지 ID는 필수")
	private UUID messageId;

	@NotNull(message = "작성자 ID는 필수")
	private UUID authorId;
}