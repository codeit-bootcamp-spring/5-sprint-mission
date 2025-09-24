package com.sprint.mission.discodeit.domain.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MessageCreateRequest {
	@NotBlank(message = "content 값은 비어있을 수 없습니다.")
	private final String content;
	@NotNull(message = "authorId는 null이 될 수 없습니다.")
	private final UUID authorId;
	@NotNull(message = "channelId는 null이 될 수 없습니다.")
	private final UUID channelId;
}
