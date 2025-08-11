package com.sprint.mission.discodeit.dto.request.message;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetMessagesByAuthorRequest {
	@NotBlank(message = "작성자 닉네임은 필수")
	private String author;

	@NotNull(message = "채널 ID는 필수")
	private UUID channelId;
}