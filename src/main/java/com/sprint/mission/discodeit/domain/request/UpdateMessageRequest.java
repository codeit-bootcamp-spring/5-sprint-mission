package com.sprint.mission.discodeit.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateMessageRequest {

	@NotBlank(message = "newContent 는 비어있을 수 없습니다.")
	private final String newContent;

	@JsonCreator
	public UpdateMessageRequest(@JsonProperty("newContent") String newContent) {
		this.newContent = newContent;
	}
}
