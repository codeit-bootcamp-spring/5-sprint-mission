package com.sprint.mission.discodeit.dto.request.message;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
public class DeleteMessageRequest {
	@NotBlank(message = "메시지 ID는 필수")
	private UUID messageId;

	@NotBlank(message = "작성자 ID는 필수")
	private UUID authorId;
}