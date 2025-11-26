package com.sprint.mission.discodeit.domain.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter

public class MessageCreateRequest {
	@NotBlank(message = "content 값은 비어있을 수 없습니다.")
	private final String content;
	@NotNull(message = "authorId는 null이 될 수 없습니다.")
	private final UUID authorId;
	@NotNull(message = "channelId는 null이 될 수 없습니다.")
	private final UUID channelId;

	@JsonCreator
	public MessageCreateRequest(

	  @JsonProperty("content") String content,
	  @JsonProperty("authorId") UUID authorId,
	  @JsonProperty("channelId") UUID channelId) {
		this.content = content;
		this.authorId = authorId;
		this.channelId = channelId;
	}
}
